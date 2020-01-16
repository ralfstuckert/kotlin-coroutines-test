import api.FakeUserService
import api.FakeUserServiceTimeBased
import api.User
import api.UserService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertSame

suspend fun loadUser(backend: UserService): User =
    withTimeout(30_000) {
        backend.load()
    }


@UseExperimental(ExperimentalCoroutinesApi::class)
class TestingTimeoutInSuspendableFunction {

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
        coEvery { backend.load() } coAnswers {
            delay(30_000)
            user
        }
        loadUser(backend)
    }

    @Test
    fun inTimeWithMockk() = runBlockingTest {
        val backend = mockk<UserService>()
        coEvery { backend.load() } coAnswers {
            delay(29_999)
            user
        }
        val loaded = loadUser(backend)
        assertSame(user, loaded)
    }


}


