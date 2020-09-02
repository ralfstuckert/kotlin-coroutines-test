package coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.test.UncaughtExceptionCaptor
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext


class SilentTestCoroutineExceptionHandler() : AbstractCoroutineContextElement(CoroutineExceptionHandler),
    CoroutineExceptionHandler, UncaughtExceptionCaptor {

    private var _uncaughtExceptions: List<Throwable> = emptyList()

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        _uncaughtExceptions = _uncaughtExceptions + exception
    }

    override val uncaughtExceptions: List<Throwable>
        get() = _uncaughtExceptions

    override fun cleanupTestCoroutines() {
        // do not rethrow caught exceptions
    }
}