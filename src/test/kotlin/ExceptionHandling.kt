import com.natpryce.hamkrest.anyElement
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.isA
import coroutines.SilentTestCoroutineExceptionHandler
import coroutines.testExceptionHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException

/**
 * Exceptions are not handled by default in a coroutine. [runBlockingTest] provides
 * a [TestCoroutineExceptionHandler] which allows you to analyze and test exceptional
 * situations.
 */
@OptIn(ExperimentalCoroutinesApi::class)
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
        runBlocking() {
            val handler = TestCoroutineExceptionHandler()
            val job = GlobalScope.launch(handler) { throw IOException("hihi") }
            job.join()

            val ex = assertThrows<IOException> {
                // cleanup will trigger rethrowing the caught exception
                handler.cleanupTestCoroutines()
            }
            assertEquals("hihi", ex.message)
        }
    }

    @Test
    fun `exceptions in global scope can be handled using runBlockingTest handler`() {
        assertThrows<IOException> {
            runBlockingTest() {
                val job = GlobalScope.launch(testExceptionHandler) { throw IOException("hihi") }
                job.join()
                assertTrue(job.isCompleted)
            }
        }
    }

    @Test
    fun `TestExceptionHandler used by runBlockingTest() provides access to all caught exceptions`() {
        assertThrows<IOException> {
            runBlockingTest() {

                launch() {
                    throw IOException()
                }
                // executed eagerly, so we can handle the exception right here
                assertEquals(1, uncaughtExceptions.size)
                assertThat(uncaughtExceptions[0], isA<IOException>())

                launch() {
                    throw IllegalArgumentException()
                }
                assertEquals(2, uncaughtExceptions.size)
                assertThat(uncaughtExceptions[1], isA<IllegalArgumentException>())
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
    fun `use a custom exception handler in runBlockingTest() for testing supervisor behaviour`() =
        runBlockingTest(SilentTestCoroutineExceptionHandler()) {
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




