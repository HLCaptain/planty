package nest.planty.ui.paired_brokers.model

import nest.planty.domain.model.DomainSensor

data class UiSensor(
    val uuid: String,
    val type: String
)

fun DomainSensor.toUiModel() = UiSensor(
    uuid = uuid,
    type = type
)
