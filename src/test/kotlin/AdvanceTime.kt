import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test

@UseExperimental(ExperimentalCoroutinesApi::class)
class AdvanceTime {

    @Test
    fun eagerUntilDelayOrYield() = runBlockingTest {
        var called = false
        launch {
            delay(1000)
            called = true
        }
        Assert.assertFalse(called)
    }

    @Test
    fun advanceTime() = runBlockingTest {
        var called = false
        launch {
            delay(1000)
            called = true
        }
        advanceTimeBy(1000)
        Assert.assertTrue(called)
    }

    @Test
    fun advanceTimeFineControl() = runBlockingTest {
        var called = false
        launch {
            delay(1000)
            called = true
        }
        advanceTimeBy(999)
        Assert.assertFalse(called)
        advanceTimeBy(1)
        Assert.assertTrue(called)
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
        Assert.assertFalse(called1)
        Assert.assertFalse(called2)

        advanceUntilIdle()
        Assert.assertTrue(called1)
        Assert.assertTrue(called2)
    }


}