package nest.planty.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Named("CoroutineDispatcherIO")
annotation class NamedCoroutineDispatcherIO

@Named("CoroutineDispatcherMain")
annotation class NamedCoroutineDispatcherMain

@Named("CoroutineDispatcherDefault")
annotation class NamedCoroutineDispatcherDefault

/**
 * No IO dispatcher in Kotlin Coroutines Core, provide platform specific implementation
 */
@Single
@NamedCoroutineDispatcherIO
expect fun provideDispatcherIO(): CoroutineDispatcher

@Single
@NamedCoroutineDispatcherMain
expect fun provideDispatcherMain(): CoroutineDispatcher

@Single
@NamedCoroutineDispatcherDefault
fun provideDispatcherDefault() = Dispatchers.Default
