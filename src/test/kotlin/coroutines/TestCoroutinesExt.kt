package coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlin.coroutines.ContinuationInterceptor

/**
 * See [issue 1609](https://github.com/Kotlin/kotlinx.coroutines/issues/1609)
 */
@UseExperimental(ExperimentalCoroutinesApi::class)
val TestCoroutineScope.testDispatcher
    get() = coroutineContext[ContinuationInterceptor] as TestCoroutineDispatcher

