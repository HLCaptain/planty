import os, sys
import time
import asyncio
import threading
import firebase_admin
from firebase_admin import firestore
from firebase_admin import credentials
import pandas as pd
import paho.mqtt.client as mqtt
import json
import ssl


class MQTT_handler:
    # The MQTT_handler class is responsible to handle the MQTT part of the broker
    def __init__(self):
        self.client = None
        self.MQTT_BROKER = "localhost"
        self.MQTT_PORT = 8883
        self.MQTT_TOPIC_SUBSCRIBE = "sensor/data"
        self.MQTT_TOPIC_PUBLISH = "device/control"
        self.MQTT_USERNAME = "server"
        self.MQTT_PASSWORD = "secretserverpass"
        self.thresholds = None
        # sets thresholds to None by default
        self.set_thresholds()

    def set_thresholds(self, newthreshold = {"temp" : None, "water" : None, "light" : None}):
        if self.thresholds is not None:
            if self.thresholds["temp"] == newthreshold["temp"] and self.thresholds["water"] == newthreshold["water"] and self.thresholds["light"] == newthreshold["light"]:
                # No changes made
                return
        # Set thresholds with the new values and publish
        print("New thresholds: " + newthreshold.__str__())
        self.thresholds = newthreshold
        if self.thresholds is not None:
            self.on_message
    
    def on_connect(self, client, userdata, flags, rc):
        print("Connected mqtt with result code "+str(rc))
        self.client.subscribe(self.MQTT_TOPIC_SUBSCRIBE)


    def on_message(self, client, userdata, msg):
        # MQTT callback function
        sensor_data = json.loads(msg.payload)

        if(sensor_data.get("pairing",False)):
            print("pairing_request 🍉")
        else:
            # Extract sensor values
            temperature = sensor_data.get("temperature", 0)
            light = sensor_data.get("light", 0)
            water = sensor_data.get("water", 0)
            
            # Determine LED states based on thresholds
            led_state = {
                "heater": None if self.thresholds["temp"] is None else temperature < self.thresholds["temp"],
                "water": None if self.thresholds["water"] is None else water < self.thresholds["water"],
                "blinder": None if self.thresholds["light"] is None else light < self.thresholds["light"]
            }
            
            print(led_state)
            self.client.publish(self.MQTT_TOPIC_PUBLISH, json.dumps(led_state))

    def start(self):
        # Starts the MQTT loop in the foreground
        self.client = mqtt.Client()

        self.client.tls_set(
            ca_certs="/etc/mosquitto/ca_certificates/ca.crt",
            tls_version=ssl.PROTOCOL_TLS,
            cert_reqs=ssl.CERT_NONE
        )

        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message

        # Set MQTT username and password
        self.client.username_pw_set(self.MQTT_USERNAME, self.MQTT_PASSWORD)

        self.client.connect(self.MQTT_BROKER, self.MQTT_PORT, 60)
        self.client.loop_start()
        print("MQTT_handler started succesfully 🙌✅")

    def stop(self):
        # Stops the MQTT loop. This MUST called when stopping the broker
        self.client.loop_stop()



class Broker:
    # The Broker class is responsible to handle the firestore part of the broker, and the pairing processes
    # It also starts the mqtt handler
    def __init__(self, uuid, ownerUUID=None):
        self.uuid = uuid
        self.ownerUUID = ownerUUID
        self.sensors = []
        self.sensor_names = []
        self.db = None
        self.broker_watch = None

        # mqtt related things:
        self.mqtt_br = None
        self.rules_watch = None
        self.mqtt_thread = None
    
    def add_data(self, col, doc, data):
        self.db.collection(col).document(doc).set(data)

    def pairing(self):
        # Check if an app is paired, and if not, starts and completes the pairing process
        if self.ownerUUID is not None and self.ownerUUID != "":
            print("Pairing already completed 🔗✅")
            return

        print("Starting the pairing process 🔗🏁")
        paired = False
        self.add_data("pairing", self.uuid, {"uuid": self.uuid, "pairingStarted": int(time.time())})
       
        # handling the pairing process
        while not paired:
            if self.ownerUUID is not None and self.ownerUUID != "":
                paired = True
                print("Completed pairing process, starting broker... 🚦")

    def create_in_database(self):
        # Searches for the broker in the database, and if it does not exists, creates it
        for f in self.db.collection("brokers").get():
            if f.to_dict()['uuid'] == self.uuid:
                print("Found the broker in the database 🧐✅")
                return
        self.add_data("brokers", self.uuid, {"ownerUUID": "", "sensors": [], "uuid": self.uuid})

    def start(self):
        # Connect to the Firestore database and listen to changes in the broker's data
        cred = credentials.Certificate("./planty-firebase-key.json")
        app = firebase_admin.initialize_app(cred)
        self.db = firestore.client()
        self.create_in_database()
        self.update_data()
        self.listen_to_broker_data()
        self.pairing()

        # Adding sensors
        self.add_sensor("temp")
        self.add_sensor("water")
        self.add_sensor("light")

        # Connect mqtt and listen to changes on both sides
        self.mqtt_br = MQTT_handler()
        self.mqtt_thread = threading.Thread(target=self.mqtt_br.start)
        self.mqtt_thread.start()
        self.listen_to_rule_changes()

        print("Broker started succesfully 🙌✅")

    def update_data(self):
        # Updates broker data from the Firestore
        broker_data = self.db.collection("brokers").document(self.uuid).get().to_dict()
        self.sensor_names = broker_data["sensors"]
        self.ownerUUID = broker_data["ownerUUID"]

    def listen_to_broker_data(self):
        # Listen for data changes in Firestore
        callback_done = threading.Event()
        doc_ref = self.db.collection("brokers").document(self.uuid)
        def on_snapshot(doc_snapshot, changes, read_time):
            self.update_data()
            callback_done.set()

        self.broker_watch = doc_ref.on_snapshot(on_snapshot)
    
    def new_rule_searcher(self, sensor_id):
        # Returns with a threshold for a specific sensor from the Firestore db's plant collection
        ret_val = None
        sensor_name = self.uuid + "_" + sensor_id
        for f in self.db.collection("plants").get():
            fd = f.to_dict()
            if fd['ownerUUID'] == self.ownerUUID and sensor_name in fd['sensors'] and fd['desiredEnvironment'] != {}:
                if sensor_id in fd['desiredEnvironment']:
                    ret_val = float(fd['desiredEnvironment'][sensor_id])
        return ret_val

    def listen_to_rule_changes(self):
        # Listen for data changes in Firestore
        callback_done = threading.Event()
        doc_ref = self.db.collection("plants")
        def on_snapshot(doc_snapshot, changes, read_time):
            # Updates thresholds in mqtt_handler
            new_temp = self.new_rule_searcher("temp")
            new_water = self.new_rule_searcher("water")
            new_light = self.new_rule_searcher("light")
            if new_temp is not None or new_water is not None or new_light is not None:
                self.mqtt_br.set_thresholds({"temp" : new_temp, "water" : new_water, "light" : new_light})
            callback_done.set()
        self.rules_watch = doc_ref.on_snapshot(on_snapshot)

    def add_sensor(self, sensor_id):
        # Adds a sensor to the broker, to the /sensors collection in Firestore, and also updates its sensors list in the /brokers collection
        sensor_name = self.uuid + "_" + sensor_id
        if sensor_name in self.sensor_names:
            return
        self.sensors.append(sensor_name)
        self.add_data("brokers", self.uuid, {"ownerUUID": self.ownerUUID, "sensors": self.sensors, "uuid": self.uuid})
        self.add_data("sensors", sensor_name, {"ownerBroker": self.uuid, "type": sensor_id, "uuid": (self.uuid + "_" + sensor_id)})
        self.update_data()

    def stop(self):
        # Stops every broker-specific process. This MUST called when stopping the broker
        if self.mqtt_br is not None:
            if self.mqtt_thread is not None:
                self.mqtt_thread.join()
            self.mqtt_br.stop()
            print("MQTT manager killed succesfully ☠️ ✨")
        if self.broker_watch is not None:
            self.broker_watch.unsubscribe()
            print("Broker subsription killed succesfully ☠️ ✨")
        if self.rules_watch is not None:
            self.rules_watch.unsubscribe()
            print("Rule watcher subsription killed succesfully ☠️ ✨")
        print("Exiting... Good bye 😘")


def main():
    # Gets a broker name from the user. then creates and starts the broker
    uuid = input("Please give this broker a name: ")
    broker = Broker(uuid)
    try:
        # Broker Setup
        broker.start()
        input("Press Enter to exit...\n")
    except KeyboardInterrupt:
        print("\nPressed Ctrl+C 🚪🏃\n")
    broker.stop()


if __name__ == '__main__':
    main()
