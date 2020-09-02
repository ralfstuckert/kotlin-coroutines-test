package coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
import kotlinx.coroutines.test.TestCoroutineScope
import kotlin.coroutines.ContinuationInterceptor

/**
 * See [issue 1609](https://github.com/Kotlin/kotlinx.coroutines/issues/1609)
 */
val TestCoroutineScope.testDispatcher
    get() = coroutineContext[ContinuationInterceptor] as? TestCoroutineDispatcher
        ?: throw IllegalArgumentException("expected TestCoroutineDispatcher in TestCoroutineScope")

val TestCoroutineScope.testExceptionHandler
    get() = coroutineContext[CoroutineExceptionHandler] as? TestCoroutineExceptionHandler
        ?: throw IllegalArgumentException("expected TestCoroutineExceptionHandler in TestCoroutineScope")

