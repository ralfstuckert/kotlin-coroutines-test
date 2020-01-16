import api.User
import api.UserRepo
import api.UserService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.*

fun CoroutineScope.loadUserAsync(backend: UserService) = async {
    withTimeout(30_000) {
        backend.load()
    }
}

fun CoroutineScope.loadUserLaunch(backend: UserService) = launch {
    withTimeout(30_000) {
        backend.load()
    }
}


@UseExperimental(ExperimentalCoroutinesApi::class)
class TestingTimeoutInSeparateCoroutine {

    @MockK
    private lateinit var backend: UserService
    @MockK
    private lateinit var repo: UserRepo

    private val user = User("Herbert")

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this)
    }


    @Test
    fun asyncInTime() = runBlockingTest {
        coEvery {backend.load() } coAnswers {
            delay(29_999)
            user
        }
        val deferred = loadUserAsync(backend)
        val loaded = deferred.await()
        assertSame(user, loaded)
    }


    @Test(expected = TimeoutCancellationException::class)
    fun asyncTimeout() = runBlockingTest {
        coEvery {backend.load() } coAnswers {
            delay(30_000)
            user
        }
        loadUserAsync(backend).await()
    }

    @Test
    fun launchTimeout() = runBlockingTest {
        coEvery {backend.load() } coAnswers {
            delay(30_000)
            user
        }
        val job = loadUserLaunch(backend)

        job.join()
        assertTrue(job.isCancelled)
    }

    @Test
    fun loadUserToRepo() = runBlockingTest {
        coEvery {backend.load() } coAnswers {
            delay(29_999)
            user
        }

        loadUserToRepo(backend, repo)

        advanceUntilIdle()
        coVerify { repo.store(user) }
    }

    @Test
    fun loadUserToRepoTimeout() = runBlockingTest {
        coEvery {backend.load() } coAnswers {
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

