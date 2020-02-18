import api.Confirmation
import api.UI
import coroutines.MainDispatcherExtension
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
@ExperimentalCoroutinesApi
class MainDispatcherUsingExtension {

    private val uiMock: UI = mockk()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `provide test dispatcher via JUnit extension (aka Rule)`(dispatcher: TestCoroutineDispatcher) =
        dispatcher.runBlockingTest {

            coEvery { uiMock.waitForUserConfirm(any()) } coAnswers {
                delay(10_000)
                Confirmation.OK
            }

            val confirmation = confirmDone(uiMock)
            // since the test dispatcher is used for main, time will be auto-advanced
            assertEquals(Confirmation.OK, confirmation)
        }

}
