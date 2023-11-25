#include "Arduino.h"
#include <WiFiClientSecure.h>
#include <WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <Adafruit_NeoPixel.h>

#include "config.hpp"

// ADC and LED strip configuration
const int adcPin1 = 4;   // ADC input 1
const int adcPin2 = 6;   // ADC input 2
const int adcPin3 = 18;  // ADC input 3
const int ledPin = 48;   // WS2812 LED "strip" pin
const int buttonPin = 0; // Pairing button
const int numLeds = 1;   // Number of LEDs in the strip

// Initialization values of the outputs
bool lastHeaterValue = false;
bool lastWaterValue = false;
bool lastBlinderValue = false;

Adafruit_NeoPixel strip = Adafruit_NeoPixel(numLeds, ledPin, NEO_GRB + NEO_KHZ800);
WiFiClientSecure espClient;
PubSubClient client(espClient);

void setup_wifi()
{
  Serial.println("Connecting to WiFi...");
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println("WiFi connected");
}

void reconnect()
{
  while (!client.connected())
  {
    Serial.print("Attempting MQTT connection...");
    if (client.connect("ESP32Client", mqtt_username, mqtt_password))
    {
      Serial.println("connected");
      client.subscribe(mqtt_topic_subscribe);
    }
    else
    {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" trying again in 3 seconds...");
      delay(3000);
    }
  }
}

void controlDevice(bool heater, bool water, bool blinder)
{
  strip.setPixelColor(0, heater ? 255 : 0, blinder ? 255 : 0, water ? 255 : 0); // Red for heater, green for blinder, blue for water
  strip.show();
  Serial.printf("Heater: %s, Water: %s, Blinder: %s\n", heater ? "ON" : "OFF", water ? "ON" : "OFF", blinder ? "ON" : "OFF");
}

void callback(char *topic, byte *payload, unsigned int length)
{
  std::string message;
  for (int i = 0; i < length; i++)
  {
    message += (char)payload[i];
  }

  Serial.println(message.c_str());
  // Parse JSON message
  StaticJsonDocument<1024> doc;
  deserializeJson(doc, message);

  if (doc.containsKey("heater") && !doc["heater"].isNull())
    lastHeaterValue = doc["heater"];
  if (doc.containsKey("water") && !doc["water"].isNull())
    lastWaterValue = doc["water"];
  if (doc.containsKey("blinder") && !doc["blinder"].isNull())
    lastBlinderValue = doc["blinder"];

  controlDevice(lastHeaterValue, lastWaterValue, lastBlinderValue);
}

void setup()
{
  Serial.begin(115200);
  setup_wifi();

  // Initialize ADC inputs
  pinMode(adcPin1, INPUT);
  pinMode(adcPin2, INPUT);
  pinMode(adcPin3, INPUT);
  pinMode(buttonPin, INPUT_PULLUP);

  // Initialize the LED strip
  strip.begin();
  strip.setBrightness(70);
  strip.show(); // Initialize all pixels to 'off'

  controlDevice(lastHeaterValue, lastWaterValue, lastBlinderValue);

  espClient.setInsecure();
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);
}

int64_t last_measurement_time = 0;

void loop()
{
  if (!client.connected())
  {
    reconnect();
  }
  client.loop();

  // Publish sensor data every 3 seconds
  if (esp_timer_get_time() - last_measurement_time > 1000 * 1000) // wait at least 1 second
  {
    last_measurement_time = esp_timer_get_time();
    // Create JSON object
    StaticJsonDocument<1024> doc;

    if (!digitalRead(buttonPin) &&
        []()
        {delay(500); return true; }() && // debouncing
        !digitalRead(buttonPin))
    {
      // Start pairing
      doc["pairing"] = true;
    }
    else
    {
      // Read sensor data
      int temp_adc = map(4095 - analogRead(adcPin1), 0, 4095, 0, 40);    // min 0 max 40
      int light_adc = map(4095 - analogRead(adcPin2), 0, 4095, 0, 5000); // min 0 max 5000
      int water_adc = map(4095 - analogRead(adcPin3), 0, 4095, 0, 100);  // min 0 max 100

      doc["temperature"] = temp_adc;
      doc["water"] = water_adc;
      doc["light"] = light_adc;
    }

    // Serialize JSON object to String
    char jsonOutput[2048];
    serializeJson(doc, jsonOutput);

    Serial.println(jsonOutput);

    // Publish sensor data
    client.publish(mqtt_topic_publish, jsonOutput);
  }
}
