package nest.planty.data.store

sealed class PlantsByUserKey {
    data class Read(val userUUID: String) : PlantsByUserKey()
    data class Write(val userUUID: String) : PlantsByUserKey()
    data class Clear(val userUUID: String) : PlantsByUserKey()
}