package nest.planty

import android.content.Context
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.initialize
import io.github.aakira.napier.Napier
import nest.planty.data.firebase.getDesktopFirebaseOptions
import nest.planty.util.log.initNapier
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule

fun main() = application {
    initNapier()
    startKoin { defaultModule() }
    initFirebase()
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

fun initFirebase() {
    initFirebasePlatform()
    initFirebaseFirestore()
    Firebase.initialize(
        context = Context(),
        options = getDesktopFirebaseOptions()
    )
}

fun initFirebasePlatform() {
    // TODO: implement Firebase local cache storage, like a SQLite table
    FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
        val storage = mutableMapOf<String, String>()
        override fun store(key: String, value: String) = storage.set(key, value)
        override fun retrieve(key: String) = storage[key]
        override fun clear(key: String) { storage.remove(key) }
        override fun log(msg: String) = Napier.d(msg)
    })
}

fun initFirebaseFirestore() {
    Firebase.firestore.setSettings(
        persistenceEnabled = false,
        sslEnabled = false,
    )
}
