package nest.planty

import android.app.Application
import nest.planty.util.log.initNapier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initNapier()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            defaultModule()
        }
    }
}