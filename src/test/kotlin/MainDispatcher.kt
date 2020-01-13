import coroutines.testDispatcher
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import kotlin.math.exp

suspend fun confirmDone(ui: UI): Confirmation =
    withContext(Dispatchers.Main) {
        ui.waitForUserConfirm("I'm done")
    }

@UseExperimental(ExperimentalCoroutinesApi::class)
class MainDispatcher {

    @MockK
    private lateinit var uiMock: UI

    private lateinit var dispatcher: TestCoroutineDispatcher

    @Before
    fun setUp() {
        // init mocks
        MockKAnnotations.init(this)

        // set test dispatcher as main
        dispatcher = TestCoroutineDispatcher()
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        // reset main dispatcher to the original Main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun showFinshed() = dispatcher.runBlockingTest {
        coEvery { uiMock.waitForUserConfirm(any()) } coAnswers { Confirmation.OK}

        val confirmation = confirmDone(uiMock)
        assertEquals(Confirmation.OK, confirmation)
    }

    @Test
    fun runBlockingTestMustBeCalledOnTestDispatcher() = /* dispatcher. */runBlockingTest {
        coEvery { uiMock.waitForUserConfirm(any()) } coAnswers {
            delay(10_000)
            Confirmation.Cancel
        }

        val confirmation = confirmDone(uiMock)
        assertEquals(Confirmation.Cancel, confirmation)
    }

    @Test
    fun useDispatcherOfTestScope() = runBlockingTest {
        Dispatchers.setMain(testDispatcher)

        coEvery { uiMock.waitForUserConfirm(any()) } coAnswers { Confirmation.OK}

        val confirmation = confirmDone(uiMock)
        assertEquals(Confirmation.OK, confirmation)
    }


}
