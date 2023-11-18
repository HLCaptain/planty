package nest.planty.data.mapping

import nest.planty.data.firestore.model.FirestoreBroker
import nest.planty.db.Broker
import nest.planty.domain.model.DomainBroker

fun Broker.toDomainModel() = DomainBroker(
    uuid = uuid,
    ownerUUID = ownerUUID,
    sensors = sensors,
)

fun DomainBroker.toNetworkModel() = FirestoreBroker(
    uuid = uuid,
    ownerUUID = ownerUUID,
    sensors = sensors,
)

fun FirestoreBroker.toLocalModel() = Broker(
    uuid = uuid,
    ownerUUID = ownerUUID,
    sensors = sensors,
)

fun Broker.toNetworkModel() = toDomainModel().toNetworkModel()
fun FirestoreBroker.toDomainModel() = toLocalModel().toDomainModel()
fun DomainBroker.toLocalModel() = toNetworkModel().toLocalModel()
