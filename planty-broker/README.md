# Planty Broker

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
