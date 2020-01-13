package coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@ExperimentalCoroutinesApi
class TestDispatcherProvider(testDispatcher: TestCoroutineDispatcher) :
    DispatcherProvider {
    override val default = testDispatcher
    override val main = testDispatcher
    override val io = testDispatcher
    override val unconfined = testDispatcher
}

@ExperimentalCoroutinesApi
fun withDispatcherProvider(context: CoroutineContext = EmptyCoroutineContext): CoroutineContext {
    val testDispatcher: TestCoroutineDispatcher =
        context.get(ContinuationInterceptor) as? TestCoroutineDispatcher ?: TestCoroutineDispatcher()
    val provider =
        context.get(DispatcherProvider) as? TestDispatcherProvider
            ?: TestDispatcherProvider(testDispatcher)
    val newContext = context + testDispatcher + provider
    return newContext
}

