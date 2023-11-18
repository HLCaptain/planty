package nest.planty.data.store

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.map
import nest.planty.data.firestore.model.FirestoreBroker
import nest.planty.data.mapping.toDomainModel
import nest.planty.data.mapping.toLocalModel
import nest.planty.data.mapping.toNetworkModel
import nest.planty.data.network.BrokerNetworkDataSource
import nest.planty.data.sqldelight.DatabaseHelper
import nest.planty.db.Broker
import nest.planty.di.NamedPlantMutableStore
import nest.planty.domain.model.DomainBroker
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
class BrokersMutableStoreBuilder(
    databaseHelper: DatabaseHelper,
    brokerNetworkDataSource: BrokerNetworkDataSource,
) {
    val store = provideBrokersMutableStore(databaseHelper, brokerNetworkDataSource)
}

@OptIn(ExperimentalStoreApi::class)
@Single
@NamedPlantMutableStore
fun provideBrokersMutableStore(
    databaseHelper: DatabaseHelper,
    brokerNetworkDataSource: BrokerNetworkDataSource,
) = MutableStoreBuilder.from(
    fetcher = Fetcher.ofFlow { key ->
        Napier.d("Fetching brokers for user $key")
        brokerNetworkDataSource.fetchByUser(userUUID = key)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { key: String ->
            databaseHelper.queryAsListFlow {
                Napier.d("Reading brokers at $key")
                it.brokerQueries.getBrokersForUser(key)
            }.map {  brokers ->
                brokers.map { it.toDomainModel() }
            }
        },
        writer = { key, local ->
            databaseHelper.withDatabase { db ->
                local.forEach {
                    Napier.d("Writing brokers at $key with $local")
                    db.brokerQueries.upsert(it)
                }
            }
            local.forEach { brokerNetworkDataSource.upsert(it.toNetworkModel()) }
        },
        delete = { key ->
            databaseHelper.withDatabase {
                Napier.d("Deleting brokers at $key")
                it.brokerQueries.deleteAllBrokersForUser(key)
            }
            brokerNetworkDataSource.deleteAll(key)
        },
        deleteAll = {
            databaseHelper.withDatabase {
                Napier.d("Deleting all brokers")
                it.brokerQueries.deleteAll()
            }
        }
    ),
    converter = Converter.Builder<List<FirestoreBroker>, List<Broker>, List<DomainBroker>>()
        .fromOutputToLocal { brokers -> brokers.map { it.toLocalModel() } }
        .fromNetworkToLocal { brokers -> brokers.map { it.toLocalModel() } }
        .build(),
).build(
    updater = Updater.by(
        post = { key, output ->
            output.forEach {
                brokerNetworkDataSource.upsert(it.toNetworkModel())
            }
            UpdaterResult.Success.Typed(output)
        },
        onCompletion = OnUpdaterCompletion(
            onSuccess = { _ ->
                Napier.d("Successfully updated broker")
            },
            onFailure = { _ ->
                Napier.d("Failed to update broker")
            }
        )
    ),
    bookkeeper = provideBookkeeper(
        databaseHelper,
        DomainBroker::class.simpleName.toString() + "List"
    ) { it }
)
