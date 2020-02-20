import api.User
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.startsWith
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@UseExperimental(ExperimentalCoroutinesApi::class)
class RunBlockingScenarios {

    @Test
    fun runBlockingWithTimeout() {
        assertThrows<TimeoutCancellationException> {

            runBlocking {
                withTimeout(2000) {
                    delay(3000)
                }
            }
        }
    }

    @Test
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