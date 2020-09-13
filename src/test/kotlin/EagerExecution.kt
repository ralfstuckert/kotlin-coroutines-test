import coroutines.AtomicBoolean
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * [runBlockingTest] actually executes launched coroutines eagerly,
 * which is effectively like starting it in mode [CoroutineStart.UNDISPATCHED].
 */
class EagerExecution {

    @Test
    fun `no eager excecution in runBlocking`() = runBlocking {
        var called by AtomicBoolean(false)
        val job = launch {
            called = true
        }
        // job might not be executed yet, so need to join
        job.join()
        assertTrue(called)
    }

    @Test
    fun `use scope to wait for coroutine`() = runBlocking {
        var called by AtomicBoolean(false)
        coroutineScope {
            val job = launch {
                called = true
            }
        }
        // coroutineScope waits for coroutine, so this is safe
        assertTrue(called)
    }

    @Test
    fun `eager excecution in runBlocking`() = runBlocking {
        var called by AtomicBoolean(false)
        val job = launch(start = CoroutineStart.UNDISPATCHED) {
            called = true
        }
        // undispatched will execute eager, so this is safe
        assertTrue(called)
    }

    @Test
    fun `eager excecution in runBlockingTest`() = runBlockingTest {
        var called by AtomicBoolean(false)
        launch {
            called = true
        }
        // runBlockingTest() uses eager executing dispatcher
        assertTrue(called)
    }

    @Test
    fun `eager execution until delay or yield`() = runBlockingTest {
        var called by AtomicBoolean(false)
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
    fun `no eager excecution on lazy start`() = runBlockingTest {
        var called by AtomicBoolean(false)
        val job = launch(start = CoroutineStart.LAZY) {
            called = true
        }
        // does not work with lazy, is this intentional?
        assertFalse(called)
    }


}