# Planty - Plant monitoring specific IoT device management system

## Administrative details

Team:
- Péter Antal (ZE4SG8)
- Dominik Buczny (C84AVJ)
- Olivér Remény (IEY82F)
- Balázs Püspök-Kiss (BL6ADS)

Instructor:
- Bányász Gábor

## Overview

The Planty project is made up from three team-made components:

- Client (A Multiplatform app)
- Broker (A Gateway between the DB and the IoT devices)
- Device (An IoT device with analog inputs and digital outputs)

and two 3rd party software:
- Firebase Firestore (database)
- Mosquitto (MQTT broker)

For each component, there is a separate documentation, that goes over their internal specifics.

This document talks about their relations, what component does what, and how do they cooperate.

![app_overview](https://github.com/HLCaptain/planty/assets/25034625/8866dacd-8658-4f65-9d0e-2df7d600e38f)


- Client
  - Authenticates the user
  - Manages the pairing process
  - Sets desired environment
  - Displays sensor values

- Broker (gateway)
  - Connects to the Firebase Firestore DB
  - Connects to the devices through the MQTT(S) broker
  - Initiates the pairing process
  - Evaluates desired states of device outputs by comparing inputs and desired enviroment variables
  - Sends the desired output state to the IoT devices

- Device (IoT device)
  - Connects to the Broker via MQTTS over the MQTT(S) broker
  - Initiates pairing process
  - Measures environment variables
  - Processes the desired output state sent from the Broker
  - Modifies its environment via its outputs

- Firestore DB
  - Stores connections between entities: Plants, Brokers and Sensors.
  - Keeps the latest read value for each sensor
  - Keeps the desired value for each sensor
  - Faciliates the communication between the client and the broker during the pairing process

- Mosquitto broker
  - Acts as an encrypted communication channel between the Broker and the IoT devices. 
  - Authenticates MQTT devices with username and password
