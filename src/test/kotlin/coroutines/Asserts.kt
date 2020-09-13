package coroutines

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.greaterThanOrEqualTo
import com.natpryce.hamkrest.lessThan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.junit.jupiter.api.fail
import org.junit.platform.commons.util.BlacklistedExceptions
import org.slf4j.LoggerFactory
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource.Monotonic

suspend inline fun <reified T : Throwable> coAssertThrows(crossinline block: suspend CoroutineScope.() -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    try {
        coroutineScope {
            block()
        }
        fail { "expected ${T::class.simpleName} to be thrown, but nothing was thrown." }
    } catch (actualException: Throwable) {
        when (actualException) {
            is T -> return actualException
            is AssertionError -> throw actualException
            else -> {
                BlacklistedExceptions.rethrowIfBlacklisted(actualException)
                throw AssertionError("unexpected exception thrown", actualException)
            }
        }
    }
}

suspend inline fun coAssertExecutesInLessThan(
    expectedDuration: Duration,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val duration = coMeasureTime(block)
    assertThat(duration, lessThan(expectedDuration)) { "execution took longer than the expected $expectedDuration" }
}


suspend inline fun coAssertExecutionTakesAtLeast(
    expectedDuration: Duration,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val duration = coMeasureTime(block)
    assertThat(
        duration,
        greaterThanOrEqualTo(expectedDuration)
    ) { "execution took less than the expected $expectedDuration" }
}

suspend inline fun coMeasureTime(crossinline block: suspend CoroutineScope.() -> Unit): Duration {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val mark = Monotonic.markNow()
    coroutineScope {
        block()
    }
    return mark.elapsedNow()
}

val log = LoggerFactory.getLogger("test")
