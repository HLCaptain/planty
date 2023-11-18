package nest.planty.ui.pairing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import nest.planty.Res
import nest.planty.domain.model.DomainBroker

class PairingScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<PairingScreenModel>()
        val pairingBrokers by screenModel.pairingBrokers.collectAsState()
        PairingBrokerList(
            brokers = pairingBrokers,
            pairBroker = screenModel::pairBroker
        )
    }

    @Composable
    fun PairingBrokerList(
        brokers: List<DomainBroker>,
        pairBroker: (uuid: String) -> Unit,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = Res.string.pairing_brokers)
            LazyColumn {
                items(brokers) { broker ->
                    BrokerCard(
                        broker = broker,
                        onClick = { pairBroker(broker.uuid) }
                    )
                }
            }
        }
    }

    @Composable
    fun BrokerCard(
        broker: DomainBroker,
        onClick: () -> Unit,
    ) {
        Card {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = broker.uuid.take(16).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                )
                TextButton(onClick = onClick) {
                    Text(text = Res.string.pair_broker)
                }
            }
        }
    }
}