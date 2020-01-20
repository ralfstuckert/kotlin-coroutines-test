import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@UseExperimental(ExperimentalCoroutinesApi::class)
class RunBlockingScenarios {

    @Test(expected = TimeoutCancellationException::class)
    fun runBlockingWithTimeout() = runBlocking {
        withTimeout(2000) {
            delay(3000)
        }
    }

    @Test(expected = TimeoutCancellationException::class)
    fun runBlockingWithTimeoutFast() = runBlockingTest {
        withTimeout(2000) {
            delay(3000)
        }
    }

}