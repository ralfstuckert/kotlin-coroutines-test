import api.FakeUserService
import api.FakeUserServiceTimeBased
import api.User
import api.UserService
import coroutines.coAssertThrows
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.platform.commons.util.BlacklistedExceptions
import org.opentest4j.AssertionFailedError

suspend fun loadUser(backend: UserService): User =
    withTimeout(30_000) {
        backend.load()
    }


@UseExperimental(ExperimentalCoroutinesApi::class)
class TestingTimeoutInSuspendableFunction {

    val user = User("Herbert")

    @Test
    fun timeoutWithFake() = runBlockingTest {
        val backend = FakeUserService(user)
        coAssertThrows<TimeoutCancellationException> {
            loadUser(backend)
        }
    }

    @Test
    fun timeoutWithTimeBasedFake() = runBlockingTest {
        val backend = FakeUserServiceTimeBased(user, 30_000)
        coAssertThrows<TimeoutCancellationException> {
            loadUser(backend)
        }
    }

    @Test
    fun inTimeWithFake() = runBlockingTest {
        val backend = FakeUserServiceTimeBased(user, 29_999)
        val loaded = loadUser(backend)
        assertSame(user, loaded)
    }

    @Test
    fun timeoutWithMockk() = runBlockingTest {
        val backend = mockk<UserService>()
        coEvery { backend.load() } coAnswers {
            delay(30_000)
            user
        }
        coAssertThrows<TimeoutCancellationException> {
            loadUser(backend)
        }
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


