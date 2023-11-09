package nest.planty.data.mapping

import dev.gitlive.firebase.auth.FirebaseUser
import nest.planty.data.firestore.model.FirestoreUser
import nest.planty.domain.model.DomainUser

fun FirestoreUser.toDomainModel() = DomainUser(
    uuid = id,
)

fun FirebaseUser.toDomainModel() = DomainUser(
    uuid = uid,
)