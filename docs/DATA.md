# Data and user flows

This document lists all the data schemes used in the Fireabse Firestore database, data sources and user flows of the app in detail.

## Data

There are a few key data structures that are used across the app. Each property is not nullable unless specified otherwise.

### Plant

A plant is a living organism that can be monitored and controlled by the app. It has a few properties:

- `uuid`: unique identifier of the plant, determined by Firebase.
- `name`: name of the plant.
- `description`: description of the plant.
- `image`: image of the plant. (May be a compressed image or a web url or an enum representing an icon, TBD)
- `sensors`: list of sensor and actuator ids that are assigned to the plant.
- `ownerUUID`: unique identifier of the owner of the plant.
- `sensorEvents`: list of `SensorEvent` that are related to the plant.
- `desiredEnvironment`: desired environment of the plant. Set by the `client`, used by the `broker` to control the plant's environment.

### Sensor

A sensor is a device that can measure the environment of a plant or and actuator that can control it. It has a few properties:

- `uuid`: unique identifier of the sensor, determined by Firebase.
- `type`: type of the sensor. (e.g. temperature, humidity, light, etc.)
- `broker`: broker id that the sensor is connected to.

### SensorEvent

A sensor event is a measurement of the environment. It has a few properties:

- `timestamp`: timestamp of the event. (Unix epoch timestamp)
- `type`: type of the event. (e.g. temperature, humidity, light, etc.)
- `value`: value of the event. (e.g. 20.0, 50.0, 100.0, can be Vector, String, etc.) Format of the value is determined by the `type` of the event.

### User

A user is a person that can use the app. It does not appear in the Firestore database, but possesses a `uid` determined by Firebase.

### Broker

A broker is a device that can sensors and actuators connect to. The broker controls and monitors the environment of the plants via the sensors and actuators. It connects to the Firestore database and uploads/listens to data. It has a few properties:

- `uuid`: unique identifier of the broker, determined by Firebase.
- `ownerUUID`: unique identifier of the owner of the broker. Can be empty if the broker is not owned by anyone.
- `sensors`: list of sensor and actuator ids that are connected to the broker.

### PairingBroker

A pairing broker is a broker that is used to pair sensors and actuators to the app. It has a few properties:

- `uuid`: unique identifier of the broker, determined by Firebase (`Broker` with the same `id` exists in another collection).
- `pairingStarted`: timestamp of when the pairing started. (Unix epoch timestamp, TBD)

## Flows

### Connecting the Broker

Connecting the broker is the process of connecting the broker to the app. It is done by the following steps:

1. The user starts the broker and connects it to the internet.
2. The broker connects to the Firestore database and adds itself to the `/brokers` collection.

### Adding a Sensor or Actuator to the Broker

Adding a sensor or actuator to the broker is the process of connecting a sensor or an actuator to the broker. It is done by the following steps:

1. The user connects the given sensor or actuator to the broker via the `MQTT` protocol.
2. The broker adds the sensor or actuator to the `/sensors` collection and updates its `sensors` list in the `/brokers` collection.

### Pairing

Pairing is the process of connecting a sensor or an actuator to the app. It is done by the following steps:

1. The user starts the pairing process in the app, which start listening to the `/pairing` collection in Firestore.
2. The user starts the pairing process to the already setup and connected broker by pressing a button on the broker (or by other means).
3. The broker creates a `PairingBroker` in the `/pairing` collection in Firestore with its own `uuid` and the `pairingStarted` timestamp.
4. The user selects the broker in the app to pair with.
5. The app assigns the user `uuid` to the `Broker` in the `/brokers` collection and removes the `PairingBroker` from the `/pairing` collection.

### Adding a Plant

Adding a plant is the process of adding a plant to the app. It is done by the following steps:

1. The user adds a plant to the app by filling out a form. The form contains the following fields:
   - `name`: name of the plant.
   - `description`: description of the plant.
   - `image`: image of the plant. (May be a compressed image or a web url, or an enum representing an icon, TBD)
2. The app adds the plant to the `/plants` collection and updates the user's `ownedPlants` list in the `/users` collection.

### Assigning Sensors and Actuators to a Plant

Assigning sensors and actuators to a plant is the process of assigning sensors and actuators to a plant. It is done by the following steps:

1. The user selects a plant in the app.
2. The app shows a list of sensors and actuators that are connected to a broker that is owned by the user.
3. The user selects a sensor or an actuator from the list.
4. The app adds the sensor or actuator's `uuid` to the plant's `sensors` list in the `/plants` collection.

### Controlling the Environment of a Plant

Controlling the environment of a plant from the app and via the broker. It is done by the following steps:

1. The user selects a plant in the app.
2. The app shows a list of environment parameters that can be controlled for the plant.
3. The user sets the parameters.
4. The app updates the plant's `desiredEnvironment` map in the `/plants` collection.
5. The broker receives the updated parameters and updates the plant's environment via any available actuators.
6. The broker listens to and updates the `sensorEvents` list for the selected plant, reflecting the changed environment.

### Remove a Sensor or Actuator from a Plant

Removing a sensor or actuator of a plant from the app. It is done by the following steps:

1. The user selects a plant in the app.
2. The app shows a list of sensors and actuators that are assigned to the plant.
3. The user selects a sensor or an actuator from the list.
4. The app removes the sensor or actuator's `uuid` from the plant's `sensors` list in the `/plants` collection.

### Remove a Plant

Removing a plant from the app. It is done by the following steps:

1. The user selects a plant in the app.
2. The app removes the plant from the `/plants` collection.
3. The broker receives that it no longer has access to the plant and no longer sends sensor events for the plant.

### Remove a Sensor or Actuator from a Broker

Removing a sensor or actuator from a broker. It is done by the following steps:

1. The user disconnects the sensor or actuator from the broker.
2. The broker notices via `MQTT` protocoll that the sensor or actuator is disconnected.
3. The broker removes the sensor or actuator's `id` from the broker's `sensors` list in the `/brokers` collection. In the same batch, it removes the sensor from the `/sensors` collection. It also removes the sensor's `uuid` from any plant's `sensors` list in the `/plants` collection it is assigned to.

### Setting Sensor Parameters (WIP, not needed for MVP)

Setting sensor parameters is the process of setting parameters of a sensor. It is done by the following steps:

1. The user selects a sensor in the app.
2. The app shows a list of parameters that can be set for the sensor.
3. The user sets the parameters.
4. The app updates the sensor's parameters in the `/sensors` collection.
5. The broker receives the updated parameters and updates the sensor's parameters.
