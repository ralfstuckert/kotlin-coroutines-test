import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@UseExperimental(ExperimentalCoroutinesApi::class)
class AdvanceTime {

    @Test
    fun eagerExecutionUntilDelayOrYield() = runBlockingTest {
        var called = false
        launch {
            yield()
            called = true
        }
        // eager execution ends at yield...
        assertFalse(called)
        // ...so continue manually
        runCurrent()
        assertTrue(called)
    }

    @Test
    fun advanceTime() = runBlockingTest {
        var called = false
        launch {
            delay(1000)
            called = true
        }
        // eager execution stops due to delay...
        assertFalse(called)
        // ...so advance virtual time
        advanceTimeBy(1000)
        assertTrue(called)
    }

    @Test
    fun advanceTimeFineControl() = runBlockingTest {
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
    fun advanceUntilIdle() = runBlockingTest {
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
    fun runBlockingWaits() = runBlocking {
        val job = launch {
            delay(5_000)
        }
    }

    @Test
    fun runBlockingTestAdvanvesUnitlIdleOnFinish() = runBlockingTest {
        launch {
            delay(5_000_000)
        }
    }


}