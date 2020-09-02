import api.Confirmation
import api.UI
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.startsWith
import coroutines.coAssertThrows
import coroutines.testDispatcher
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

suspend fun confirmDone(ui: UI): Confirmation =
    withContext(Dispatchers.Main) {
        ui.waitForUserConfirm("Press OK to continue")
    }

/**
 * UI code like e.g. Android, Swing, JavaFX is executed by a dedicated
 * UI-Thread. When using coroutines you have to use the [Main dispatcher][Dispatchers.Main]
 * In order to use the Test-Dispatcher the coroutines test package provides a
 * special function [Dispatchers.setMain]. The following examples show how you
 * may use this function with either a dedicated dispatcher or the one provided by
 * [runBlockingTest].
 */
class MainDispatcher {

    private val uiMock: UI = mockk()
    private lateinit var dedicatedTestDispatcher: TestCoroutineDispatcher

    @BeforeEach
    fun setUp() {
        clearAllMocks()

        dedicatedTestDispatcher = TestCoroutineDispatcher()
    }

    @AfterEach
    fun tearDown() {
        // reset main dispatcher to the original Main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `you must provide a main dispatcher in tests`() = runBlockingTest {
        coEvery { uiMock.waitForUserConfirm(any()) } coAnswers {
            delay(10_000)
            Confirmation.OK
        }

        val ex = coAssertThrows<java.lang.IllegalStateException> {
            confirmDone(uiMock)
        }
        assertThat(ex.message ?: "", startsWith("Module with the Main dispatcher is missing"))
    }


    @Test
    fun `use dispatcher of test scope for main`() = runBlockingTest {
        Dispatchers.setMain(testDispatcher)

        coEvery { uiMock.waitForUserConfirm(any()) } coAnswers {
            delay(10_000)
            Confirmation.OK
        }

        val confirmation = confirmDone(uiMock)
        // since the test dispatcher is used for main, time will be auto-advanced
        assertEquals(Confirmation.OK, confirmation)

        // main dispatcher will be reset in tearDown()
    }

    /**
     * In order to use the [TestCoroutineDispatcher] in [runBlockingTest] you have to
     * pass it as coroutine context.
     */
    @Test
    fun `provide test dispatcher as context`() = runBlockingTest(dedicatedTestDispatcher) {
        Dispatchers.setMain(dedicatedTestDispatcher)

        coEvery { uiMock.waitForUserConfirm(any()) } coAnswers {
            delay(10_000)
            Confirmation.OK
        }

        val confirmation = confirmDone(uiMock)
        // since the test dispatcher is used for main, time will be auto-advanced
        assertEquals(Confirmation.OK, confirmation)
    }


    /**
     * Another way to use the [TestCoroutineDispatcher] is to call [TestCoroutineDispatcher.runBlockingTest]
     * which effectively passes it as context like before.
     */
    @Test
    fun `call runBlockingTest() on dedicated dispatcher`() = dedicatedTestDispatcher.runBlockingTest {
        Dispatchers.setMain(dedicatedTestDispatcher)

        coEvery { uiMock.waitForUserConfirm(any()) } coAnswers {
            delay(10_000)
            Confirmation.OK
        }

        val confirmation = confirmDone(uiMock)
        // since the test dispatcher is used for main, time will be auto-advanced
        assertEquals(Confirmation.OK, confirmation)
    }

    /**
     * In this example, the test dispatcher created in setup is not provided to [runBlockingTest],
     * therefore it will create its own. As a result, things like time control won't work, since
     * they rely on the dispatcher.
     */
    @Test
    fun `If dedicated test dispatcher is not provided, runBlockingTest() will create another one`() {
        val ex = assertThrows<IllegalStateException> {

            runBlockingTest {
                Dispatchers.setMain(dedicatedTestDispatcher)

                coEvery { uiMock.waitForUserConfirm(any()) } coAnswers {
                    delay(10_000)
                    Confirmation.Cancel
                }

                val confirmation = confirmDone(uiMock)
                assertEquals(Confirmation.OK, confirmation)
            }

        }
        // auto-advancing failed since a different dispatcher was used
        assertThat(ex.message ?: "", startsWith("This job has not completed yet"))
    }


}


