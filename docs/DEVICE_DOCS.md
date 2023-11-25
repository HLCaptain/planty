# Device documentation

This document goes over the implemented hardware and firmware of the Planty Demo device.

## Hardware

- Main module: ESP32-S3-DevKitC-1 v1.1
- 3 Analog inputs (connected to 3pcs 10K Potentiometers connected to GPIO 4-6-18)
- 1 Digital input (button connected to GPIO 0)
- 3 Digital outputs (1pcs onboard WS2812 RGB LED connected to GPIO48)
- 802.11 b/g/n WLAN (also known as WiFi - security protocol is supported up to WPA3-SAE)

## Firmware

- Runtime framework: platformio/espressif32@6.4.0 (Arduino v2.0.11 as a component in ESP-IDF v5.1.1)
- Development/Build framework: Platformio
- WS led driver: Adafruit_NeoPixel
- TLS+WiFi library: WiFiClientSecure
- MQTT library: PubSubClient
- Communication protocol: MQTTS (Port 8883)
- Application level protocol: JSON

## Build & Flashing instructions
  1. Download Visual Studio Code ([https://code.visualstudio.com/download](https://code.visualstudio.com/download))
  2. Install the "PlatformIO" extension
  3. Open the project folder in the editor
  4. Run "Upload" or "Upload and Monitor" command from the sidebar (They are in the esp32-s3-devkitc-1 > General section.)

## Configuration
The configurable (runtime constant) values of the device are in the `config.hpp` file, in the `src` folder.

Parameter list, description and default values: 
```cpp
// WiFi credentials
const char *ssid = "demo"; // WiFi name
const char *password = "12345679"; // WiFI password

// MQTT Broker settings
const char *mqtt_server = "157.90.162.198"; // Server's IP where the MQTT broker listens
const uint16_t mqtt_port = 8883; // The MQTT broker's listening port

// MQTT user settings
const char *mqtt_username = "planty_device_1"; // username for MQTT authentication
const char *mqtt_password = "secretpassword"; // password for MQTT authentication

// Application settings
const char *mqtt_topic_publish = "sensor/data"; // sending topic name
const char *mqtt_topic_subscribe = "device/control"; // recieving topic name
```

## Application level protocol

The device waits for an object with three bool values: `{"heater": true, "water": false, "blinder": false}`

If a value is not available on server, it can send "null" as the value instead of true or false, in this case, the outputs do not change.
If a key is missing, the output that would be controlled by it, it will not change the output.

The device sends two types of messages:
- Environmental data `{"temperature":0,"water":100,"light":5000}`
- Pairing initiation command `{"pairing":true}`

Examples:

```txt
DEVICE: {"temperature":0,"water":100,"light":5000}
SERVER: {"heater": true, "water": false, "blinder": false}
STATUS: Heater: ON, Water: OFF, Blinder: OFF

DEVICE: {"temperature":0,"water":100,"light":5000}
SERVER: {"heater": true, "water": false, "blinder": false}
STATUS: Heater: ON, Water: OFF, Blinder: OFF

DEVICE: {"temperature":9,"water":100,"light":5000}
SERVER: {"heater": true, "water": false, "blinder": false}
STATUS: Heater: ON, Water: OFF, Blinder: OFF

DEVICE: {"temperature":15,"water":100,"light":5000}
SERVER: {"heater": true, "water": false, "blinder": false}
STATUS: Heater: ON, Water: OFF, Blinder: OFF

DEVICE: {"temperature":20,"water":100,"light":5000}
SERVER: {"heater": false, "water": false, "blinder": false}
STATUS: Heater: OFF, Water: OFF, Blinder: OFF

DEVICE: {"temperature":23,"water":100,"light":5000}
SERVER: {"heater": false, "water": false, "blinder": false}
STATUS: Heater: OFF, Water: OFF, Blinder: OFF
...
```

- `DEVICE:`: What the device sends
- `SERVER:`: What the sever sends
- `STATUS:`: What the device sets on its outputs

Note, that there is no one to one connection between messages, both the device and the server si able to (and allowed to) send messages anytime. For example this is valid:

```txt
DEVICE: {"temperature":0,"water":100,"light":5000}
DEVICE: {"temperature":0,"water":100,"light":5000}
DEVICE: {"temperature":9,"water":100,"light":5000}
DEVICE: {"temperature":15,"water":100,"light":5000}
DEVICE: {"temperature":20,"water":100,"light":5000}
SERVER: {"heater": false, "water": false, "blinder": false}
DEVICE: {"temperature":23,"water":100,"light":5000}
DEVICE: {"temperature":23,"water":100,"light":5000}
...
```

## Processes

This workflow describes the fundamental operations of the Planty Demo IoT device.

![planty_device_logic_flow](https://github.com/HLCaptain/planty/assets/25034625/bee6bd29-0a68-4952-ba3a-73137bf1d530)

Upon startup, the device initiates its WiFi stack to enable wireless communication. Subsequently, it configures its inputs and outputs - the analog GPIO pins for the potentiometer and the digital output for the RGB LED.

Following the hardware setup, the device initializes its MQTTS (MQTT over SSL/TLS) stack, preparing for secure message-based communication. It then attempts to connect to an MQTT broker (That is also the server that is able to communicat with the Firestore DB, but it does not have to.)

If the device successfully connects to the broker, it enters a polling loop, checking for incoming MQTT messages using the client.loop() function. When a message arrives, the device parses the message and adjusts its outputs accordingly, e.g. changing the color of an LED based on the received instructions.

After that the device monitors for the activation of the pair button. (next to reset button, labeled as "BOOT") If this button is pressed, it triggers the pairing process.

When the pair button is not pressed, the device starts to gather "environmental data", (which are analog readings from the potentiometers' output). This data is then transmitted to the MQTT broker.

After completing these tasks, the device enters a "sleep mode" (delay) to conserve energy and resources and "wakes up" periodically to maintain its connection to the MQTT broker or when user interaction occurs.
