package coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.test.UncaughtExceptionCaptor
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext


class SilentTestCoroutineExceptionHandler() : AbstractCoroutineContextElement(CoroutineExceptionHandler),
        CoroutineExceptionHandler, UncaughtExceptionCaptor {

    private val _exceptions = mutableListOf<Throwable>()

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        synchronized(_exceptions) {
            _exceptions += exception
        }
    }

    override val uncaughtExceptions: List<Throwable>
        get() = synchronized(_exceptions) { _exceptions.toList() }

    override fun cleanupTestCoroutines() {
        // do not rethrow caught exceptions
    }
}