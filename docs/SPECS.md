# Specifications

Team:

- Péter Antal (ZE4SG8)
- Dominik Buczny (C84AVJ)
- Olivér Remény (IEY82F)
- Balázs Püspök-Kiss (BL6ADS)

Planty is a plant health monitoring system. It aims to help you keep your plants healthy and happy. It offers a mobile app to monitor your plants' health. You have the option to integrate sensors and actuators to both monitor and regulate the plant's environment. For example, you can measure and control soil moisture, light intensity, temperature, and humidity with the right sensors and actuators.

## Architecture

Architecture can be broken down into a few key layers:

- Client
- Cloud
- Broker
- Sensors and Actuators

### Client

The client component is a mobile app that provides users the ability to access and modify data stored in the cloud. (Features are described as user stories below.) It can be a simple mobile app with push notifications and a simple UI.

### Cloud

The cloud component authenticates and also hosts a database to store data received from the broker. It can be a simple database like MongoDB or a cloud service like Firebase. The data can be accessed and modified both by the client, and the IoT devices with the help of the Broker. (If the entity is authorized to do so.)

A concrete solution for this is the Google Cloud Firestore, (with Firebase Authentication support).

### Broker

The broker acts as a server, collecting data from sensors and then storing it in the cloud database. It also controls the actuators to meet user-set requirements, like desired temperature, soil moisture, etc. It communicates with the sensors and actuators via the MQTT protocol.

The broker's job is to:
- Act as a gateway, providing a stable connection point for the on-field device fleet.
  - And also provide the server a way to communicate with the on-field devices. 
- Keep track of connection parameters, and status, including (for example):
  - Connection keys, and tokens (Both server side, and on-field device side.)
  - Availability, latency, and error statistics.
  - Message queue (two-way buffering messages until acknowledgment or timeout happens, although this may be internal to the connection protocol.)
 
#### Connection
The suggested protocol to communicate with IoT devices in this case is **MQTT**.
Specs:
| Feature/Specification | Description |
|-----------------------|-------------|
| Protocol Type         | Publish/Subscribe |
| Default Port          | 1883 (TCP), 8883 (TLS/SSL) |
| Payload Format        | Binary (can be any format, in our case, it probably will be JSON) |
| Maximum Payload Size  | 256 MB (but often limited by client/server implementations) |

Communication needs to be encrypted. The precise method for achieving this is still under consideration, as it depends on the support package provided by IDF (IoT Development Framework). There is a chance that we will implement encryption with a 3rd party library to avoid the overhead of TLS on the IoT devices. (The available libraries on the broker side should not be a limiting factor, as the scenario of *"ESP side supports it, but there is no Python lib for it"* is very unlikely, and probably should be avoided.)

### IoT device: Sensors and Actuators

Sensors and actuators connect to the broker using the MQTT protocol. Sensors send data to the broker and actuators receive (request) commands from the broker.

#### MCU
The chosen microcontroller family is the [ESP32](https://www.espressif.com/en/products/socs/esp32).

Specs (Not everything, just what we will probably use):
|Spec|Value|
|:-|-|
|CPU|2 core, 240 MHz|
|RAM|~500 KB|
|I/O|GPIO I2C SPI UART ADC... (And many more. These are just what we will probably utilize.)|
|Connection| WiFi b/g/n (2.4GHz only)|
|SDK|ESP-IDF (and maybe Arduino as an IDF module)|
|OS|FreeRTOS|

#### Peripherals

##### Inputs
Planty hardware should either utilize actual sensors, or if it is better for demo or testing purposes, manual inputs (Setting sensor values).
Some sensors like temperature and light intensity are easily measurable with sensors, but there are other types of sensors (soil moisture level sensor for example) that would make testing and implementation of the hardware unnecessarily complicated. **The analog inputs may be replaced by potentiometers and discrete inputs with switches.**

##### Outputs/actuators/manipulators
The primary purpose of the Planty Hardware is to take actions based on its measurements.
In practical scenarios, actions are executed through valves, switches, and lamps, but here (for easier testing and implementation) most/all of them will be replaced by LEDs or some other display type, that shows each output's state clearly and in a space-efficient manner.

## User Stories

### Adding a plant

As a user, I want to add a plant to the app so that I can monitor its health via sensors.

### Adding a sensor

As a user, I want to add a sensor to the app so that I can monitor my plant's health.

### Provision plant

As a user, I want to provision my plant to keep it healthy. In case the moisture level is too low, I want to water it. In case the light intensity is too low, I want to turn on the grow light. I want to set and connect the sensors and actuators to monitor and control my plant via the client app.

### Changing plant settings

As a user, I want to change the settings of my plant so that I can adjust the environment to the plant's needs. When I change the settings, I want to see the changes in the app and real life (e.g. water pump starts watering the plant if soil moisture is too low).
