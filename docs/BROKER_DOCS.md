# Broker documentation

This document goes over the implemented software of the Planty Demo broker and its enviroment.

## Resources

The program is tested and fully working in this enviroment:
- 1vCPU (Intel Skylake (GEN 6) model 85, 2GHz)
- 2GB RAM (Although the measured maximum memory usage of the process was around 50MB)
- 50 Mbps internet access (The real usage should be less than 10KBps)
- Ubuntu 22.04.03 LTS

Softwares needed to be installed
- Mosquitto (MQTT broker)
- Python 3 & pip (Runtime)
 - Python packages paho-mqtt 

Other requirements:
- One public IPv4 address that is static, or made quasi-static with the help of a Dynamic DNS service
- Publicly accessible 8883 port for incoming MQTTS messages
- A certificate for secure communication.

# Installation instructions

The following script installs the required software packages, opens port 8883, creates and self-signs a certificate and sets up Mosquitto to listen on 8883 (secure port) handle messages from the server and a device.

Apart from that, the python script is to be loaded and started, maybe set up to auto-start and restart the service with `systemctl`.

```bash
#!/bin/bash

# Install Python3, pip, and Mosquitto
apt update
apt install python3 pip mosquitto -y
pip install paho-mqtt

# Navigate to Mosquitto certificates directory
cd /etc/mosquitto/certs

# Generate CA Key and Certificate
openssl req -new -x509 -days 365 -extensions v3_ca -keyout ca.key -out ../ca_certificates/ca.crt

# Create Server Key and Certificate Signing Request (CSR)
openssl genrsa -out server.key 2048
openssl req -out server.csr -key server.key -new

# Sign the Server CSR with the CA Key
openssl x509 -req -in server.csr -CA ../ca_certificates/ca.crt -CAkey ca.key -CAcreateserial -out server.crt -days 365

# Enable all trafic on port 8883
ufw enable
ufw allow 8883

# Set appropriate permissions for the server key
sudo chown mosquitto:mosquitto server.key
sudo chmod 400 server.key

# Mosquitto configuration settings
echo "listener 8883 0.0.0.0
allow_anonymous false
password_file /etc/mosquitto/password_file
cafile /etc/mosquitto/ca_certificates/ca.crt
certfile /etc/mosquitto/certs/server.crt
keyfile /etc/mosquitto/certs/server.key
require_certificate false" | sudo tee /etc/mosquitto/conf.d/szarch.conf

# Create users
echo "server:secretserverpass
planty_device_1:secretpassword" |sudo tee /etc/mosquitto/password_file

# Hash passwords
mosquitto_passwd -U /etc/mosquitto/password_file

# Restart Mosquitto to apply the changes
sudo systemctl restart mosquitto
```
