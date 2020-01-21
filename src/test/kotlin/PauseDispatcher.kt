import api.UserRepo
import api.UserService
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@UseExperimental(ExperimentalCoroutinesApi::class)
class PauseDispatcher {

    @BeforeEach
    fun setup() {
        // TODO
        MockKAnnotations.init(this)
    }

    @MockK
    lateinit var userServiceMock: UserService

    @MockK
    lateinit var userRepoMock: UserRepo

    @Test
    fun runCurrentAdvancesUntilCurrentTime() = runBlockingTest {
        var state = 0

        launch {
            yield()
            state = 1
            yield()
            state = 2
            yield()
            state = 3
        }
        // launched eager, but stops at yield
        assertEquals(0, state)

        runCurrent()
        // runCurrent() advances all action until current (virtual) time
        assertEquals(3, state)
    }




    @Test
    fun pauseDispatcher() = runBlockingTest {
        var state = 0

        pauseDispatcher()
        launch {
            state = 1
            delay(1000)
            state = 2
            delay(1000)
            state = 3
        }
        // not started yet
        assertEquals(0, state)

        runCurrent()
        assertEquals(1, state)
        advanceTimeBy(1000)
        assertEquals(2, state)
        advanceTimeBy(1000)
        assertEquals(3, state)
    }

    @Test
    fun resumeDispatcher() = runBlockingTest {
        var state = 0

        pauseDispatcher()
        launch {
            state = 1
            delay(1000)
            state = 2
            delay(1000)
            state = 3
        }
        // not started yet
        assertEquals(0, state)

        runCurrent()
        assertEquals(1, state)
        advanceTimeBy(1000)
        assertEquals(2, state)

        resumeDispatcher()
        // immediate dispatch after resumeDispatcher() 
        assertEquals(3, state)
    }


    @Test
    fun pauseDispatcherBlock() = runBlockingTest {
        var state = 0

        pauseDispatcher {
            launch {
                state = 1
                delay(1000)
                state = 2
                delay(1000)
                state = 3
            }

            assertEquals(0, state)
            runCurrent()
            assertEquals(1, state)
            advanceTimeBy(1000)
            assertEquals(2, state)
        }

        // immediate dispatch after pauseDispatcher() block
        assertEquals(3, state)
    }


}