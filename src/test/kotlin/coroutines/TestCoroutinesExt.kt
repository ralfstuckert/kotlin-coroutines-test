package coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
import kotlinx.coroutines.test.TestCoroutineScope
import kotlin.coroutines.ContinuationInterceptor
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * See [issue 1609](https://github.com/Kotlin/kotlinx.coroutines/issues/1609)
 */
@UseExperimental(ExperimentalCoroutinesApi::class)
val TestCoroutineScope.testDispatcher
    get() = coroutineContext[ContinuationInterceptor] as? TestCoroutineDispatcher ?:
    throw IllegalArgumentException( "expected TestCoroutineDispatcher in TestCoroutineScope")

@UseExperimental(ExperimentalCoroutinesApi::class)
val TestCoroutineScope.testExceptionHandler
    get() = coroutineContext[CoroutineExceptionHandler] as? TestCoroutineExceptionHandler ?:
    throw IllegalArgumentException( "expected TestCoroutineExceptionHandler in TestCoroutineScope")

