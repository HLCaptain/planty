package nest.planty.data.mapping

import nest.planty.data.firestore.model.FirestorePlant
import nest.planty.db.Plant
import nest.planty.domain.model.DomainPlant
import nest.planty.domain.model.DomainSensor

fun Plant.toDomainModel(sensors: List<DomainSensor>) = DomainPlant(
    uuid = uuid,
    ownerUUID = ownerUUID,
    name = name,
    description = description,
    desiredEnvironment = desiredEnvironment,
    sensorEvents = sensorEvents,
    sensors = sensors,
    image = image,
)

fun DomainPlant.toNetworkModel() = FirestorePlant(
    uuid = uuid,
    ownerUUID = ownerUUID,
    name = name,
    description = description,
    desiredEnvironment = desiredEnvironment,
    sensorEvents = sensorEvents.map { it.toNetworkModel() },
    sensors = sensors.map { it.uuid },
    image = image,
)

fun FirestorePlant.toLocalModel() = Plant(
    uuid = uuid,
    ownerUUID = ownerUUID,
    name = name,
    description = description,
    desiredEnvironment = desiredEnvironment,
    sensorEvents = sensorEvents.map { it.toDomainModel() },
    sensors = sensors,
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
    image = image,
)

fun DomainPlant.toLocalModel() = toNetworkModel().toLocalModel()
fun FirestorePlant.toDomainModel(sensors: List<DomainSensor>) = toLocalModel().toDomainModel(sensors)
