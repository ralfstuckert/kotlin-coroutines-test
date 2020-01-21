import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@UseExperimental(ExperimentalCoroutinesApi::class)
class EagerExecution {

    @Test
    fun noEagerExcecutionInRunBlocking() = runBlocking {
        var called = false
        val job = launch {
            called = true
        }
        // job might not be executed yet, so need to join
        job.join()
        assertTrue(called)
    }

    @Test
    fun useScopeToWaitForCoroutine() = runBlocking {
        var called = false
        coroutineScope {
            val job = launch {
                called = true
            }
        }
        // outer scope waits for coroutine, so this is safe
        assertTrue(called)
    }

    @Test
    fun eagerExcecutionInRunBlocking() = runBlocking {
        var called = false
        val job = launch(start = CoroutineStart.UNDISPATCHED) {
            called = true
        }
        // undispatched will execute eager, so this is safe
        assertTrue(called)
    }

    @Test
    fun eagerExcecutionInRunBlockingTest() = runBlockingTest {
        var called = false
        launch {
            called = true
        }
        // runBlockingTest() uses eager executing dispatcher
        assertTrue(called)
    }

    @Test
    fun noEagerExcecutionOnLazyStart() = runBlockingTest {
        var called = false
        val job = launch(start = CoroutineStart.LAZY) {
            called = true
        }
        // does not work with lazy, is this intentional?
        assertFalse(called)
    }


}