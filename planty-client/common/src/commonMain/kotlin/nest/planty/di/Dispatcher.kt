package nest.planty.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named

@Named("CoroutineDispatcherIO")
annotation class NamedCoroutineDispatcherIO

@Named("CoroutineDispatcherMain")
annotation class NamedCoroutineDispatcherMain

/**
 * No IO dispatcher in Kotlin Coroutines Core, provide platform specific implementation
 */
@Factory
@NamedCoroutineDispatcherIO
expect fun provideDispatcherIO(): CoroutineDispatcher

@Factory
@NamedCoroutineDispatcherMain
fun provideDispatcherMain() = Dispatchers.Main
