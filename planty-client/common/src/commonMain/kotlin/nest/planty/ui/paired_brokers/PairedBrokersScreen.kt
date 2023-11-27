package nest.planty.ui.paired_brokers

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import nest.planty.Res
import nest.planty.ui.paired_brokers.model.UiBroker

class PairedBrokersScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<PairedBrokersScreenModel>()
        val ownedBrokers by screenModel.ownedBrokers.collectAsState()
        Column(
            modifier = Modifier
                .padding(8.dp)
                .animateContentSize(),
        ) {
            OwnedBrokerList(
                ownedBrokers = ownedBrokers,
                disownBroker = screenModel::disownBroker
            )
        }
    }

    @Composable
    fun OwnedBrokerList(
        ownedBrokers: List<UiBroker>,
        disownBroker: (uuid: String) -> Unit,
    ) {
        Crossfade(
            modifier = Modifier.animateContentSize(),
            targetState = ownedBrokers.isEmpty()
        ) {
            if (it) {
                Text(
                    text = Res.string.no_paired_brokers,
                    style = MaterialTheme.typography.headlineLarge
                )
            } else {
                Column {
                    Text(
                        text = Res.string.brokers,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    LazyColumn(
                        modifier = Modifier
                            .animateContentSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(ownedBrokers) { broker ->
                            BrokerCard(
                                broker = broker,
                                disownBroker = { disownBroker(broker.uuid) }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun BrokerCard(
        broker: UiBroker,
        disownBroker: () -> Unit
    ) {
        Card {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = broker.uuid.take(16),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Button(onClick = disownBroker) {
                        Text(text = Res.string.unpair_broker)
                    }
                }
                Column {
                    broker.sensors.forEach { sensor ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = sensor.uuid.take(16),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = sensor.type,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}