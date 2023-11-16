package nest.planty.data.sqldelight

import app.cash.sqldelight.ColumnAdapter
import nest.planty.domain.model.DomainSensorEvent

val mapAdapter = object : ColumnAdapter<Map<String, String>, String> {
    override fun decode(databaseValue: String): Map<String, String> {
        if (databaseValue.isEmpty()) return emptyMap()
        return databaseValue.split(",").associate {
            val (key, value) = it.split("=")
            key to value
        }
    }

    override fun encode(value: Map<String, String>): String {
        return value.map { "${it.key},${it.value}" }.joinToString(",")
    }
}

val listAdapter = object : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> {
        if (databaseValue.isEmpty()) return emptyList()
        return databaseValue.split(",")
    }

    override fun encode(value: List<String>): String {
        return value.joinToString(",")
    }
}

val sensorEventAdapter = object : ColumnAdapter<List<DomainSensorEvent>, String> {
    override fun decode(databaseValue: String): List<DomainSensorEvent> {
        if (databaseValue.isEmpty()) return emptyList()
        return databaseValue.split(",").map {
            val (type, timestamp, value) = it.split("=")
            DomainSensorEvent(type, timestamp.toLong(), value)
        }
    }

    override fun encode(value: List<DomainSensorEvent>): String {
        return value.joinToString(",")
    }
}