package coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

interface DispatcherProvider : CoroutineContext.Element {

    companion object Key : CoroutineContext.Key<DispatcherProvider>

    override val key: CoroutineContext.Key<*> get() = DispatcherProvider

    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

object DefaultDispatcherProvider : DispatcherProvider {
    override val default = Dispatchers.Default
    override val main = Dispatchers.Main
    override val io = Dispatchers.IO
    override val unconfined = Dispatchers.Unconfined
}

val CoroutineScope.dispatcherProvider: DispatcherProvider
    get() = coroutineContext.dispatcherProvider

val CoroutineContext.dispatcherProvider: DispatcherProvider
    get() = get(DispatcherProvider) ?: DefaultDispatcherProvider


