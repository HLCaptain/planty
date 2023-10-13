# Planty Hardware

This document summarizes the hardware specific details of the project.

## MCU
The chosen microcontroller family is the [ESP32](https://www.espressif.com/en/products/socs/esp32).

Specs (Not everything, just what we will probably use):
|Spec|Value|
|:-|-|
|CPU|2 core, 240 MHz|
|RAM|~500 KB|
|I/O|GPIO I2C SPI UART ADC... (and many more, these are just what we will probably utilize)|
|Connection| WiFi b/g/n (2.4GHz only)|
|SDK|ESP-IDF (and maybe Arduino as an IDF module)|
|OS|FreeRTOS|

## Peripherals

### Inputs
Planty hardware should either utilize real sensors or if it is better for demo or testing purposes manual inputs (Setting sensor values).
Some sensors like temperature and light intensity are easily measurable with sensors, but there are other types of sensors (soil moisture level sensor for example) which would make testing and implementation of the hardware unneccessairly complicated. Depending on the available supplies at the time of order, **the anaolg inputs will be replaced by potentiometers and discrete inputs into switches.**

### Outputs / actuators / manipulators
Planty hardware's reason is to act based on it's measurements. (The loop to the server is to keep the logic out of the device.)
These are done mostly valves, switches and lamps in a real scenario, but here (for easier testing and implementation) most/all of them will be replaced by LED diodes or some other display, that shows each outputs' state clearly and in a space efficient manner.

## Connection
The connection with the server should be made over the internet. The suggested protocol is MQTT.
MQTT specs:
| Feature/Specification | Description |
|-----------------------|-------------|
| Protocol Type         | Publish/Subscribe |
| Default Port          | 1883 (TCP), 8883 (TLS/SSL) |
| Payload Format        | Binary (can be any format, in our case it probably will be JSON) |
| Maximum Payload Size  | 256 MB (but often limited by client/server implementations) |

The communication should be encrypted. The exact way of doing this is yet to be decided as it depends on the support package provided by IDF. (Also the available libraries on the broker side, although the scenario of *"ESP side supports it, but there is no Python lib for it"* is very unlikely, and probably should be avoided.)
