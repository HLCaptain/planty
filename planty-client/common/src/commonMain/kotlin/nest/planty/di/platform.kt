package nest.planty.di

import kotlinx.coroutines.CoroutineDispatcher

/**
 * No IO dispatcher in Kotlin Coroutines Core, provide platform specific implementation
 */
expect fun provideDispatcherIO(): CoroutineDispatcher
