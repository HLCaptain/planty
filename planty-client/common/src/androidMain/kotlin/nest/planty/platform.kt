package nest.planty

actual fun getPlatformName(): String {
    return "Android"
}

actual typealias JavaSerializable = java.io.Serializable