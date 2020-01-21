package coroutines

import kotlinx.coroutines.CoroutineScope
import org.junit.jupiter.api.fail
import org.junit.platform.commons.util.BlacklistedExceptions


inline suspend fun <reified T : Throwable> coAssertThrows(noinline block: suspend () -> Unit): T {
    try {
        block()
        fail { "expected ${T::class.simpleName} to be thrown, but nothing was thrown." }
    } catch (actualException: Throwable) {
        return if (actualException is T) {
            actualException
        } else {
            BlacklistedExceptions.rethrowIfBlacklisted(actualException)
            throw AssertionError("unexpected exception thrown", actualException)
        }
    }
}


