package nest.planty.di

import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named

@Named("CoroutineScopeIO")
annotation class NamedCoroutineScopeIO

@Named("CoroutineScopeMain")
annotation class NamedCoroutineScopeMain

@Factory
@NamedCoroutineScopeIO
fun provideCoroutineScopeIO() = CoroutineScope(provideDispatcherIO())

@Factory
@NamedCoroutineScopeMain
fun provideCoroutineScopeMain() = CoroutineScope(provideDispatcherMain())