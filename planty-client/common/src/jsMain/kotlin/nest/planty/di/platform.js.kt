package nest.planty.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * No IO dispatcher on JS, so we use the default dispatcher
 */
actual fun provideDispatcherIO(): CoroutineDispatcher = Dispatchers.Default
