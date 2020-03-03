import api.User
import api.UserRepo
import api.UserService
import coroutines.coAssertThrows
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

fun CoroutineScope.loadUserAsync(backend: UserService):Deferred<User> = async {
    withTimeout(30_000) {
        backend.load()
    }
}

fun CoroutineScope.loadUserLaunch(backend: UserService):Job = launch {
    withTimeout(30_000) {
        backend.load()
    }
}


/**
 * Testing timeout in new coroutines is quites similar to
 * testing in [suspendable functions][TestingTimeoutInSuspendableFunction]
 */
@UseExperimental(ExperimentalCoroutinesApi::class)
class TestingTimeoutInSeparateCoroutine {

    private val backend: UserService = mockk()
    private val repo: UserRepo = mockk()
    private val user = User("Herbert")

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }


    @Test
    fun `testing async in time`() = runBlockingTest {
        coEvery { backend.load() } coAnswers {
            delay(29_999)
            user
        }
        val deferred = loadUserAsync(backend)
        // waiting will advance coroutine
        val loaded = deferred.await()
        assertSame(user, loaded)
    }


    @Test
    fun `testing async timeout`() = runBlockingTest {
        coEvery { backend.load() } coAnswers {
            delay(30_000)
            user
        }
        coAssertThrows<TimeoutCancellationException> {
            loadUserAsync(backend).await()
        }
    }

    @Test
    fun `testing launch timeout`() = runBlockingTest {
        coEvery { backend.load() } coAnswers {
            delay(30_000)
            user
        }
        val job = loadUserLaunch(backend)
        job.join()
        // join() does not throw CancellationException
        assertTrue(job.isCancelled)
    }

    @Test
    fun `testing behaviour using mocks`() = runBlockingTest {
        coEvery { backend.load() } coAnswers {
            delay(29_999)
            user
        }

        loadUserToRepo(backend, repo)

        advanceUntilIdle()
        coVerify { repo.store(user) }
    }

    @Test
    fun `testing behaviour on timeout using mocks`() = runBlockingTest {
        coEvery { backend.load() } coAnswers {
            delay(30_000)
            user
        }

        loadUserToRepo(backend, repo)

        advanceUntilIdle()
        coVerify(exactly = 0) { repo.store(user) }
    }

}


fun CoroutineScope.loadUserToRepo(backend: UserService, repo: UserRepo) = launch {
    val user = withTimeout(30_000) {
        backend.load()
    }
    repo.store(user)
}

