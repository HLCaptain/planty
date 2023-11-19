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
class SensorMutableStoreBuilder(
    databaseHelper: DatabaseHelper,
    sensorNetworkDataSource: SensorNetworkDataSource,
) {
    val store = provideSensorMutableStore(databaseHelper, sensorNetworkDataSource)
}

@OptIn(ExperimentalStoreApi::class)
fun provideSensorMutableStore(
    databaseHelper: DatabaseHelper,
    sensorNetworkDataSource: SensorNetworkDataSource,
) = MutableStoreBuilder.from(
    fetcher = Fetcher.ofFlow { key ->
        Napier.d("Fetching sensor with key $key")
        sensorNetworkDataSource.fetch(uuid = key)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { key: String ->
            databaseHelper.queryAsOneFlow {
                Napier.d("Reading sensor at $key")
                it.sensorQueries.select(key)
            }.map { it.toDomainModel() }
        },
        writer = { key, local ->
            databaseHelper.withDatabase { db ->
                Napier.d("Writing sensor at $key with $local")
                db.sensorQueries.upsert(local)
            }
        },
        delete = { key ->
            databaseHelper.withDatabase {
                Napier.d("Deleting sensor at $key")
                it.sensorQueries.delete(key)
            }
            sensorNetworkDataSource.delete(key)
        },
        deleteAll = {
            databaseHelper.withDatabase {
                Napier.d("Deleting all sensor")
                it.sensorQueries.deleteAll()
            }
        }
    ),
    converter = Converter.Builder<FirestoreSensor, Sensor, DomainSensor>()
        .fromOutputToLocal { it.toLocalModel() }
        .fromNetworkToLocal { it.toLocalModel() }
        .build(),
).build(
    updater = Updater.by(
        post = { key, output ->
            sensorNetworkDataSource.upsert(output.toNetworkModel())
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
        DomainSensor::class.simpleName.toString()
    ) { it }
)
