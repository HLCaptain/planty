package nest.planty.ui.paired_brokers.model

import nest.planty.domain.model.DomainBroker

data class UiBroker(
    val uuid: String,
    val sensors: List<UiSensor>,
)

fun DomainBroker.toUiModel(sensors: List<UiSensor>) = UiBroker(
    uuid = uuid,
    sensors = sensors.filter { sensor -> sensor.uuid in this.sensors }
)
