package nest.planty.data.mapping

import nest.planty.data.firestore.model.FirestoreSensorEvent
import nest.planty.domain.model.DomainSensorEvent

fun DomainSensorEvent.toNetworkModel() = FirestoreSensorEvent(
    type = type,
    timestamp = timestamp,
    value = value,
)

fun FirestoreSensorEvent.toDomainModel() = DomainSensorEvent(
    type = type,
    timestamp = timestamp,
    value = value,
)