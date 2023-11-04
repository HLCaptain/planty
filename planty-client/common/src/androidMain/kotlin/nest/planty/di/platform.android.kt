package nest.planty.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual fun provideDispatcherIO(): CoroutineDispatcher = Dispatchers.IO
