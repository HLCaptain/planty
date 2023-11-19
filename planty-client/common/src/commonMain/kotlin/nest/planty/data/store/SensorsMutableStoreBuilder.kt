package nest.planty.data.store

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.map
import nest.planty.data.firestore.model.FirestoreSensor
import nest.planty.data.mapping.toDomainModel
import nest.planty.data.mapping.toLocalModel
import nest.planty.data.mapping.toNetworkModel
import nest.planty.data.network.SensorNetworkDataSource
import nest.planty.data.sqldelight.DatabaseHelper
import nest.planty.db.Sensor
import nest.planty.domain.model.DomainSensor
import org.koin.core.annotation.Single
import org.mobilenativefoundation.store.store5.Converter
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MutableStoreBuilder
import org.mobilenativefoundation.store.store5.OnUpdaterCompletion
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Updater
import org.mobilenativefoundation.store.store5.UpdaterResult

@OptIn(ExperimentalStoreApi::class)
@Single
class SensorsMutableStoreBuilder(
    databaseHelper: DatabaseHelper,
    sensorNetworkDataSource: SensorNetworkDataSource,
) {
    val store = provideSensorsMutableStore(databaseHelper, sensorNetworkDataSource)
}

@OptIn(ExperimentalStoreApi::class)
@Single
fun provideSensorsMutableStore(
    databaseHelper: DatabaseHelper,
    sensorNetworkDataSource: SensorNetworkDataSource,
) = MutableStoreBuilder.from(
    fetcher = Fetcher.ofFlow { key ->
        Napier.d("Fetching sensor for broker $key")
        sensorNetworkDataSource.fetchByBroker(brokerUUID = key)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { key: String ->
            databaseHelper.queryAsListFlow {
                Napier.d("Reading sensor at $key")
                it.sensorQueries.getSensorsForBroker(key)
            }.map { sensors ->
                sensors.map { it.toDomainModel() }
            }
        },
        writer = { key, local ->
            databaseHelper.withDatabase { db ->
                local.forEach {
                    Napier.d("Writing sensor at $key with $local")
                    db.sensorQueries.upsert(it)
                }
            }
        },
        delete = { key ->
            databaseHelper.withDatabase {
                Napier.d("Deleting sensor at $key")
                it.sensorQueries.delete(key)
            }
        },
        deleteAll = {
            databaseHelper.withDatabase {
                Napier.d("Deleting all sensor")
                it.sensorQueries.deleteAll()
            }
        }
    ),
    converter = Converter.Builder<List<FirestoreSensor>, List<Sensor>, List<DomainSensor>>()
        .fromOutputToLocal { sensors -> sensors.map { it.toLocalModel() } }
        .fromNetworkToLocal { sensors -> sensors.map { it.toLocalModel() } }
        .build(),
).build(
    updater = Updater.by(
        post = { key, output ->
            output.forEach {
                sensorNetworkDataSource.upsert(it.toNetworkModel())
            }
            UpdaterResult.Success.Typed(output)
        },
        onCompletion = OnUpdaterCompletion(
            onSuccess = { _ ->
                Napier.d("Successfully updated sensor")
            },
            onFailure = { _ ->
                Napier.d("Failed to update sensor")
            }
        )
    ),
    bookkeeper = provideBookkeeper(
        databaseHelper,
        DomainSensor::class.simpleName.toString() + "List"
    ) { it }
)
