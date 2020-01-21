import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import java.util.concurrent.Executors
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@UseExperimental(ExperimentalCoroutinesApi::class)
class ExceptionHandling {

    private val customDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()


    @Test//(expected = IllegalArgumentException::class)
    fun runBlockingMayIgnoreExceptions2():Unit = runBlocking {
        val job1 = launch(customDispatcher) {
            throw IllegalArgumentException("first")
        }
        val job2 = launch(customDispatcher) {
            throw IllegalArgumentException("second")
        }
        val job3 = launch(customDispatcher) {
            throw IllegalArgumentException("third")
        }
    }

    @Test//(expected = IllegalArgumentException::class)
    fun runBlockingTestGetsThemAll2():Unit = runBlockingTest {
        val job1 = launch(customDispatcher) {
            throw IllegalArgumentException("first")
        }

        assertEquals(listOf(IllegalArgumentException("first")), uncaughtExceptions)

        val job2 = launch(customDispatcher) {
            throw IllegalArgumentException("second")
        }
        val job3 = launch(customDispatcher) {
            throw IllegalArgumentException("third")
        }
    }

    @Test
    fun runBlockingMayIgnoreExceptions():Unit = runBlocking() {
//        val job = launch {
            val j2 = launch {
//                assertTrue("1") { false }
                throw IllegalArgumentException("hihi")
            }
            j2.join()
            delay(100)
//            assertTrue("2", false)
            cancel("I can't stand it anymore")
//        }.join()
    }

    @Test//(expected = IllegalArgumentException::class)
    fun runBlockingTestGetsThemAll():Unit = runBlockingTest {
        val job = launch() {
            launch(customDispatcher) {
//                assertTrue("1", false)
//                throw IllegalArgumentException("hihi")
            }
            delay(100)
//            assertTrue("2",false)
            cancel("I can't stand it anymore")
        }
    }

}