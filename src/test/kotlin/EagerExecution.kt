import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@UseExperimental(ExperimentalCoroutinesApi::class)
class EagerExecution {

    @Test
    fun noEagerExcecutionInRunBlocking() = runBlocking {
        var called = false
        val job = launch {
            called = true
        }
        job.join()
        assertTrue(called)
    }

    @Test
    fun eagerExcecutionInRunBlocking() = runBlocking {
        var called = false
        val job = launch(start = CoroutineStart.UNDISPATCHED) {
            called = true
        }
//        job.join()
        assertTrue(called)
    }

    @Test
    fun eagerExcecution() = runBlockingTest {
        var called = false
        launch {
            called = true
        }
        assertTrue(called)
    }

    @Test
    fun noEagerExcecutionOnLazyStart() = runBlockingTest {
        var called = false
        val job = launch(start = CoroutineStart.LAZY) {
            called = true
        }
        assertFalse(called)
    }
}