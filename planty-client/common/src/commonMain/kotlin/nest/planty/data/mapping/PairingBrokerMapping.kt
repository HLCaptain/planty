package nest.planty.data.mapping

import nest.planty.data.firestore.model.FirestorePairingBroker
import nest.planty.domain.model.DomainPairingBroker

fun DomainPairingBroker.toNetworkModel() = FirestorePairingBroker(
    uuid = uuid,
    pairingStarted = pairingStarted,
)

fun FirestorePairingBroker.toDomainModel() = DomainPairingBroker(
    uuid = uuid,
    pairingStarted = pairingStarted,
)
