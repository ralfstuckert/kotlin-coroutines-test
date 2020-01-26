package coroutines

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.lessThan
import org.junit.jupiter.api.fail
import org.junit.platform.commons.util.BlacklistedExceptions
import java.time.Instant

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

inline suspend fun coAssertRunsIn(millis: Long, noinline block: suspend () -> Unit) {
    val start = Instant.now().toEpochMilli()
    block()
    val duration = Instant.now().toEpochMilli() - start
    assertThat(duration, lessThan(millis)) { "execution took longer than the expected $millis ms" }
}


