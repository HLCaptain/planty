package nest.planty.data.mapping

import nest.planty.data.firestore.model.FirestorePlant
import nest.planty.db.Plant
import nest.planty.domain.model.DomainPlant

fun FirestorePlant.toLocalModel() = Plant(
    uuid = uuid,
    ownerUUID = ownerUUID,
    name = name,
    description = description,
    desiredEnvironment = desiredEnvironment,
    sensorEvents = sensorEvents.map { it.toLocalModel() },
    sensors = sensors,
    brokers = brokers,
    image = image,
)

fun Plant.toNetworkModel() = FirestorePlant(
    uuid = uuid,
    ownerUUID = ownerUUID,
    name = name,
    description = description,
    desiredEnvironment = desiredEnvironment,
    sensorEvents = sensorEvents.map { it.toNetworkModel() },
    sensors = sensors,
    brokers = brokers,
    image = image,
)

fun DomainPlant.toLocalModel() = Plant(
    uuid = uuid,
    ownerUUID = ownerUUID,
    name = name,
    description = description,
    desiredEnvironment = desiredEnvironment,
    sensorEvents = sensorEvents,
    sensors = sensors,
    brokers = brokers,
    image = image,
)