import com.natpryce.hamkrest.anyElement
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.isA
import coroutines.SilentTestCoroutineExceptionHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.io.IOException

@UseExperimental(ExperimentalCoroutinesApi::class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ExceptionHandling {


    @Test
    fun `ruBlocking() allways throws Exception`() {
        assertThrows<IOException> {
            runBlocking() {
                val handler = CoroutineExceptionHandler { _, exception ->
                    println("ignoring $exception")
                }
                val job = launch(handler) { throw IOException("hihi") }
            }
        }
    }

    @Test
    fun `exceptions in global scope are not handled by runBlocking()`() = runBlocking() {
        val job = GlobalScope.launch() { throw IOException("hihi") }
        job.join()
    }

    @Test
    fun `exceptions in global scope can be handled with a TestCoroutineExceptionHandler`() {
        assertThrows<IOException> {
            runBlocking() {
                val handler = TestCoroutineExceptionHandler()
                val job = GlobalScope.launch(handler) { throw IOException("hihi") }
                job.join()
                // cleanup will trigger rethrowing the caught exception
                handler.cleanupTestCoroutines()
            }
        }
    }

    @Test
    fun `exceptions in global scope can be handled using runBlockingTest handler`() {
        assertThrows<IOException> {
            runBlockingTest() {
                val handler = coroutineContext[CoroutineExceptionHandler]
                requireNotNull(handler)
                val job = GlobalScope.launch(handler) { throw IOException("hihi") }
                job.join()
                assertTrue(job.isCompleted)
            }
        }
    }

    @Test
    fun `children of supervisor job do not propagate exception`() = runBlocking() {
        supervisorScope() {
            val child = launch() {
                throw IOException()
            }
        }
    }

    @Test
    fun `TestCoroutineExceptionHandler will propagate exceptions of supervised children`() {
        assertThrows<IOException> {
            runBlockingTest() {
                // implicitly use the TestCoroutineExceptionHandler of runBlockingTest()
                supervisorScope() {
                    val child = launch() {
                        throw IOException()
                    }
                }
            }
        }
    }

    @Test
    fun `use a custom exception handler in runBlockingTest() for testing supervisor behaviour`() = runBlockingTest(SilentTestCoroutineExceptionHandler()) {
        supervisorScope() {
            val child = launch() {
                throw IOException()
            }
        }
        // custom exception handler does not propagate (throw) exception as expected
        // in supervisor scope, but we still can examine it in the test
        assertThat(uncaughtExceptions, anyElement(isA<IOException>()))
    }


}




