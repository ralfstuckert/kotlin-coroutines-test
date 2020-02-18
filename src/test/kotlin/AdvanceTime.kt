import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.greaterThanOrEqualTo
import coroutines.coAssertExecutesInLessThan
import coroutines.coAssertExecutionTakesAtLeast
import coroutines.log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime
import kotlin.time.seconds


@UseExperimental(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class AdvanceTime {

    @Test
    @Disabled
    fun `using runBlocking() may take some time`() = runBlocking() {
        coAssertExecutionTakesAtLeast(5.seconds) {
            delay(5_000)
        }
    }

    @Test
    @Disabled
    fun `runBlockingTest() auto-advances virtual time`() = runBlockingTest {
        coAssertExecutesInLessThan(2.seconds) {
            val virtualStart = currentTime
            delay(100_000)
            val virtualDuration = currentTime - virtualStart
            assertThat(virtualDuration, greaterThanOrEqualTo(100_000L))
        }
    }


    @Test
    fun `does not auto-advance time in launched coroutine`() = runBlockingTest {
        var called = false
        log.info("before launch")
        launch {
            log.info("delay")
            delay(1000)
            called = true
        }
        log.info("after launch")
        // eager execution stops due to delay...
        assertFalse(called)
        // ...so advance virtual time
        advanceTimeBy(1000)
        assertTrue(called)
    }

    @Test
    @Disabled
    fun `advance time is reliable`() = runBlockingTest {
        var called = false
        launch {
            delay(1000)
            called = true
        }
        // control of virtual time is reliable
        advanceTimeBy(999)
        assertFalse(called)
        advanceTimeBy(1)
        assertTrue(called)
    }

    @Test
    @Disabled
    fun `advanceUntilIdle() tries to run all coroutines until idle`() = runBlockingTest {
        var called1 = false
        var called2 = false
        launch {
            delay(1000)
            called1 = true
        }
        launch {
            delay(2000)
            called2 = true
        }
        assertFalse(called1)
        assertFalse(called2)

        // try to advance all coroutines
        advanceUntilIdle()
        assertTrue(called1)
        assertTrue(called2)
    }

    @Test
    @Disabled
    fun `runBlockingTest() calls advanceUntilIdle() on finish`() = runBlockingTest {
        launch {
            delay(5_000_000)
        }
    }


}