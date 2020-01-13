import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.*

@UseExperimental(ExperimentalCoroutinesApi::class)
class AdvanceTime {

    @Test
    fun eagerUntilDelayOrYield() = runBlockingTest {
        var called = false
        launch {
            delay(1000)
            called = true
        }
        assertFalse(called)
    }

    @Test
    fun advanceTime() = runBlockingTest {
        var called = false
        launch {
            delay(1000)
            called = true
        }
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

        advanceUntilIdle()
        assertTrue(called1)
        assertTrue(called2)
    }


}