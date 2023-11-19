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
class BrokerMutableStoreBuilder(
    databaseHelper: DatabaseHelper,
    brokerNetworkDataSource: BrokerNetworkDataSource,
) {
    val store = provideBrokerMutableStore(databaseHelper, brokerNetworkDataSource)
}

@OptIn(ExperimentalStoreApi::class)
@Single
fun provideBrokerMutableStore(
    databaseHelper: DatabaseHelper,
    brokerNetworkDataSource: BrokerNetworkDataSource,
) = MutableStoreBuilder.from(
    fetcher = Fetcher.ofFlow { key ->
        Napier.d("Fetching broker with key $key")
        brokerNetworkDataSource.fetch(uuid = key)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { key: String ->
            databaseHelper.queryAsOneFlow {
                Napier.d("Reading broker at $key")
                it.brokerQueries.select(key)
            }.map { it.toDomainModel() }
        },
        writer = { key, local ->
            databaseHelper.withDatabase { db ->
                Napier.d("Writing broker at $key with $local")
                db.brokerQueries.upsert(local)
            }
            brokerNetworkDataSource.upsert(local.toNetworkModel())
        },
        delete = { key ->
            databaseHelper.withDatabase {
                Napier.d("Deleting broker at $key")
                it.brokerQueries.delete(key)
            }
            brokerNetworkDataSource.delete(key)
        },
        deleteAll = {
            databaseHelper.withDatabase {
                Napier.d("Deleting all brokers")
                it.brokerQueries.deleteAll()
            }
        }
    ),
    converter = Converter.Builder<FirestoreBroker, Broker, DomainBroker>()
        .fromOutputToLocal { it.toLocalModel() }
        .fromNetworkToLocal { it.toLocalModel() }
        .build(),
).build(
    updater = Updater.by(
        post = { key, output ->
            brokerNetworkDataSource.upsert(output.toNetworkModel())
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
        DomainBroker::class.simpleName.toString()
    ) { it }
)
