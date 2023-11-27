# Planty Broker

## Overview

The broker's job is to:
- Act as a gateway, provide a stable connnection point for the on-field device fleet.
  - And also provide the server a way to communicate with the on-field devices. 
- Keep track of connection parameters, and status, including (for example):
  - Connection keys, tokens (Both server side, and on-field device side.)
  - Availability, latency and error statistics.
  - Message queue (two-way buffering messages until acknowledgement or timeout happens, this may be internal of the connection protocol.)
- Provide information for the server about connected devices.
- Request data and send commands to the devices. (The server's job is to configure the broker, which devices need to be read, and which devices should act something based on the server's model of the real world.)

The broker will run on a much more performant host, than the ESP32 hardware it communicates with, so it is not constrained by most means.

The communication protocol towards the on-field hardware is MQTT. The communication should be encrypted some way.

The communication protocol towards the server is TBD.

This document describes the design and functionality of a broker system implemented in Python. The broker integrates MQTT (Message Queuing Telemetry Transport) for handling real-time data from sensors and controls devices, and Firestore, a flexible, scalable database for cloud solutions.

### Components

1. **MQTT Handler**: Handles MQTT communication, including data subscription, message handling, and publishing control commands based on sensor data.
2. **Broker**: Manages Firestore database operations and oversees the pairing process of devices. It also initializes and manages the MQTT handler.

## MQTT Handler

### Purpose

The MQTT handler is responsible for real-time communication with IoT devices. It subscribes to sensor data and publishes control commands based on predefined thresholds.

### Key Functions

- `set_thresholds`: Sets or updates thresholds for temperature, water, and light.
- `on_connect`: Establishes MQTT connection and subscribes to sensor data topic.
- `on_message`: Processes incoming sensor data and publishes control messages if thresholds are exceeded.
- `start`: Initializes the MQTT client with secure connection settings and starts the MQTT loop.
- `stop`: Stops the MQTT client loop and cleans up resources.

### Structure and Rationale

The handler encapsulates MQTT-specific operations to separate concerns and simplify maintenance. Using class methods for key functionalities like message handling and connection setup allows for easy modification and testing.

## Broker

### Purpose

The Broker class manages the Firestore database, handles the pairing process of new devices, and initializes the MQTT handler for sensor and actuator data management.

### Key Functions

- `add_data`: Adds or updates data in Firestore.
- `pairing`: Manages the pairing process of the broker with a user's account.
- `create_in_database`: Ensures the broker's presence in the Firestore database.
- `start`: Initializes Firestore connection, sensor management, and starts the MQTT handler.
- `stop`: Gracefully shuts down all operations, including Firestore listeners and MQTT handler.
- `listen_to_broker_data`, `listen_to_rule_changes`: Listen to data and rule changes in Firestore for dynamic updates.

### Structure and Rationale

The Broker class serves as a central point for managing interactions between Firestore and MQTT. This design provides a clear separation between real-time data handling (MQTT) and persistent data management (Firestore), enhancing maintainability and scalability.

## Integration and Workflow

1. **Initialization**: The `main` function creates a Broker instance and starts its operation.
2. **Pairing**: The Broker pairs with a user account if not already paired.
3. **Database Sync**: Broker syncs with Firestore for existing configuration and listens for real-time updates.
4. **MQTT Operations**: Simultaneously, the MQTT handler starts, subscribes to sensor topics, and begins publishing control commands based on sensor data and Firestore rules.
