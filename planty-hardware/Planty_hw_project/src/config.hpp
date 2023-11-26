#pragma once

// WiFi credentials
const char *ssid = "demo";
const char *password = "12345679";

// MQTT Broker settings
const char *mqtt_server = "157.90.162.198";
const uint16_t mqtt_port = 8883;

// MQTT user settings
const char *mqtt_username = "planty_device_1";
const char *mqtt_password = "secretpassword";

// Application settings
const char *mqtt_topic_publish = "sensor/data";
const char *mqtt_topic_subscribe = "device/control";