package nest.planty.data.sqldelight

import app.cash.sqldelight.ColumnAdapter
import nest.planty.data.model.SensorEvent

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

val sensorEventAdapter = object : ColumnAdapter<List<SensorEvent>, String> {
    override fun decode(databaseValue: String): List<SensorEvent> {
        if (databaseValue.isEmpty()) return emptyList()
        return databaseValue.split(",").map {
            val (type, timestamp, value) = it.split("=")
            SensorEvent(type, timestamp.toLong(), value)
        }
    }

    override fun encode(value: List<SensorEvent>): String {
        return value.joinToString(",")
    }
}