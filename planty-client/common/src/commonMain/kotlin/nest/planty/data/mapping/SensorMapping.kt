package nest.planty.data.mapping

import nest.planty.data.firestore.model.FirestoreSensor
import nest.planty.db.Sensor
import nest.planty.domain.model.DomainSensor

fun Sensor.toDomainModel() = DomainSensor(
    uuid = uuid,
    ownerBroker = ownerBroker,
    type = type,
)

fun FirestoreSensor.toLocalModel() = Sensor(
    uuid = uuid,
    ownerBroker = ownerBroker,
    type = type,
)

fun DomainSensor.toNetworkModel() = FirestoreSensor(
    uuid = uuid,
    ownerBroker = ownerBroker,
    type = type,
)

fun Sensor.toNetworkModel() = toDomainModel().toNetworkModel()
fun FirestoreSensor.toDomainModel() = toLocalModel().toDomainModel()
fun DomainSensor.toLocalModel() = toNetworkModel().toLocalModel()

