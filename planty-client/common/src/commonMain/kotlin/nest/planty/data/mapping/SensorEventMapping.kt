package nest.planty.data.mapping

import nest.planty.data.firestore.model.FirestoreSensorEvent
import nest.planty.data.model.SensorEvent

fun SensorEvent.toNetworkModel() = FirestoreSensorEvent(
    type = type,
    timestamp = timestamp,
    value = value,
)

fun FirestoreSensorEvent.toLocalModel() = SensorEvent(
    type = type,
    timestamp = timestamp,
    value = value,
)