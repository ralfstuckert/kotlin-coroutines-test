import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@UseExperimental(ExperimentalCoroutinesApi::class)
class RunBlockingScenarios {

    @Test//(expected = TimeoutCancellationException::class)
    fun runBlockingWithTimeout() {
        assertThrows<TimeoutCancellationException> {

            runBlocking {
                withTimeout(2000) {
                    delay(3000)
                }
            }
        }
    }

    @Test//(expected = TimeoutCancellationException::class)
    fun runBlockingWithTimeoutFast() {
        assertThrows<TimeoutCancellationException> {
            runBlockingTest {
                withTimeout(2000) {
                    delay(3000)
                }
            }
        }
    }

}