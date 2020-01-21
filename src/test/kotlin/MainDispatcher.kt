import api.Confirmation
import api.UI
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.contains
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.startsWith
import coroutines.coAssertThrows
import coroutines.testDispatcher
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        ui.waitForUserConfirm("I'm done")
    }

@ExperimentalCoroutinesApi
class MainDispatcher {

    @MockK
    private lateinit var uiMock: UI

    private lateinit var dedicatedTestDispatcher: TestCoroutineDispatcher

    @BeforeEach
    fun setUp() {
        // TODO mock init Hauer
        // init mocks
        MockKAnnotations.init(this)

        // set test dispatcher as main
        dedicatedTestDispatcher = TestCoroutineDispatcher()
//        Dispatchers.setMain(dedicatedTestDispatcher)
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
        assertThat(ex.message?:"", startsWith("Module with the Main dispatcher is missing"))
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
     * One way to use the [TestCoroutineDispatcher] is to call [TestCoroutineDispatcher.runBlockingTest]
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
     * Another way is to pass the [TestCoroutineDispatcher] as coroutine context to
     * [runBlockingTest]
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
     * In this example, the test dispatcher created in setup is not provided to [runBlockingTest],
     * therefore it will create its own. As a result, things like time control won't work, since
     * they rely on the dispatcher.
     */
    @Test//(expected = IllegalStateException::class)
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
        assertThat(ex.message?:"", startsWith("This job has not completed yet"))
    }


}
