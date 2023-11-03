package nest.planty.util.log

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

// TODO: log based on user analytics preference
fun initNapier() {
    Napier.takeLogarithm()
    Napier.base(DebugAntilog())
    Napier.base(CrashlyticsAntilog())
}