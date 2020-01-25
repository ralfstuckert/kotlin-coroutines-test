import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.util.concurrent.Executors
import kotlin.coroutines.ContinuationInterceptor

@UseExperimental(ExperimentalCoroutinesApi::class)
class ExceptionHandling {

    private val customDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

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
                val handler = coroutineContext[CoroutineExceptionHandler] as TestCoroutineExceptionHandler
                val job = GlobalScope.launch(handler) { throw IOException("hihi") }
                job.join()
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
    fun `handler of runBlockingTest() will propagate exceptions of supervisor job children`() {
        assertThrows<IOException> {
            runBlockingTest() {
                supervisorScope() {
                    val child = launch() {
                        throw IOException()
                    }
                }
            }
        }
    }


}