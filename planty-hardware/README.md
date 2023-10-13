# Planty Hardware

This document summarizes the hardware specific details of the project.

## MCU
The chosen microcontroller family is the [ESP32](https://www.espressif.com/en/products/socs/esp32).

Specs: (Not everything, just what we will probably use):
|Spec|Value|
|:-|-|
|CPU|2 core, 240 MHz|
|RAM|~500 KB|
|I/O|GPIO I2C SPI UART ADC... (and many more, these are just what we will probably utilize)|
|Connection| WiFi b/g/n (2.4GHz only)|
|SDK|ESP-IDF (and maybe Arduino as an IDF module)|
|OS|FreeRTOS|