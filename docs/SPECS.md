# Specifications

Team:

- Péter Antal (ZE4SG8)
- Dominik Buczny (C84AVJ)
- Olivér Remény (IEY82F)
- Balázs Püspök-Kiss (BL6ADS)

Planty is a plant health monitoring system. Its aim is to help you keep your plants healthy and happy. It offers a mobile app to monitor your plants' health. You can add sensors and hardware (actuators) to monitor and control the plant's environment. For example, you can measure and control the soil moisture, light intensity, temperature, and humidity with the right sensors and actuators.

## Architecture

Architecture can be broken down to a few key layers:

- Client
- Cloud
- Broker
- Sensors and Actuators

### Client

Client is a mobile app that allows the user to access and modify data on the cloud. It can be a simple mobile app with push notifications and a simple UI.

### Cloud

Cloud can authenticate and host a database that stores the data from the broker. It can be a simple database like MongoDB or a cloud service like Firebase. This data then can be accessed and modified by the client.

### Broker

Broker is a server that receives data from the sensors and stores it in the cloud database. It also controls the actuators to meet user set requirements, like desired temperature, soil moisture, etc. It communicates via MQTT protocol with the sensors and actuators.

### Sensors and Actuators

Sensors and actuators are connected to the broker via MQTT protocol. Sensors send data to the broker and actuators receive commands from the broker.

## User Stories

### Adding a plant

As a user, I want to add a plant to the app so that I can monitor its health via sensors.

### Adding a sensor

As a user, I want to add a sensor to the app so that I can monitor my plant's health.

### Provision plant

As a user, I want to provision my plant to keep it healthy. In case the moisture level is too low, I want to water it. In case the light intensity is too low, I want to turn on the grow light. I want to set and connect the sensors and actuators to monitor and control my plant via the client app.

### Changing plant settings

As a user, I want to change the settings of my plant so that I can adjust the environment to the plant's needs. When I change the settings, I want to see the changes in the app and in real-life (e.g. water pump starts watering the plant if soil moisture is too low).
