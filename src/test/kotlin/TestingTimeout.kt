import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertSame
import org.junit.Test

suspend fun loadUser(backend: UserService): User =
    withTimeout(30_000) {
        backend.load()
    }

fun CoroutineScope.loadUserToRepo(backend: UserService, repo:UserRepo) = launch {
    val user = withTimeout(30_000) {
        backend.load()
    }
    repo.store(user)
}


@UseExperimental(ExperimentalCoroutinesApi::class)
class TestingTimeout {

    val user = User("Herbert")

    @Test(expected = TimeoutCancellationException::class)
    fun timeoutWithFake() = runBlockingTest {
        val backend = FakeUserService(user)
        loadUser(backend)
    }

    @Test(expected = TimeoutCancellationException::class)
    fun timeoutWithTimeBasedFake() = runBlockingTest {
        val backend = FakeUserServiceTimeBased(user, 30_000)
        loadUser(backend)
    }

    @Test
    fun inTimeWithFake() = runBlockingTest {
        val backend = FakeUserServiceTimeBased(user, 29_999)
        val loaded = loadUser(backend)
        assertSame(user, loaded)
    }

    @Test(expected = TimeoutCancellationException::class)
    fun timeoutWithMockk() = runBlockingTest {
        val backend = mockk<UserService>()
        coEvery {backend.load() } coAnswers {
            delay(30_000)
            user
        }
        loadUser(backend)
    }

    @Test
    fun inTimeWithMockk() = runBlockingTest {
        val backend = mockk<UserService>()
        coEvery {backend.load() } coAnswers {
            delay(29_999)
            user
        }
        val loaded = loadUser(backend)
        assertSame(user, loaded)
    }


    @Test
    fun loadUserToRepo() = runBlockingTest {
        val backend = mockk<UserService>()
        val repo = mockk<UserRepo>()
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
        val backend = mockk<UserService>()
        val repo = mockk<UserRepo>()
        coEvery {backend.load() } coAnswers {
            delay(30_000)
            user
        }

        loadUserToRepo(backend, repo)
        advanceUntilIdle()
        coVerify(exactly = 0) { repo.store(user) }
    }

}

