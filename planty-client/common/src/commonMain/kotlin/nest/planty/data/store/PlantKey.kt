package nest.planty.data.store

sealed class PlantKey {
    data class Read(val uuid: String) : PlantKey()
    data class Write(val uuid: String) : PlantKey()
    data class Clear(val uuid: String) : PlantKey()
}