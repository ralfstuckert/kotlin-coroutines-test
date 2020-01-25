import api.FakeUserServiceCompletableDeferred
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

suspend fun loadUser(backend: UserService): User =
    withTimeout(30_000) {
        backend.load()
    }


@UseExperimental(ExperimentalCoroutinesApi::class)
class TestingTimeoutInSuspendableFunction {

    val user = User("Herbert")

    @Test
    fun `testing timeout with a fake service using completable deferred`() = runBlockingTest {
        val backend = FakeUserServiceCompletableDeferred(user)
        coAssertThrows<TimeoutCancellationException> {
            loadUser(backend)
        }
    }

    @Test
    fun `testing timeout with a fake service using delay`() = runBlockingTest {
        val backend = FakeUserServiceTimeBased(user, 30_000)
        coAssertThrows<TimeoutCancellationException> {
            loadUser(backend)
        }
    }

    @Test
    fun `testing in time with a fake service using delay`() = runBlockingTest {
        val backend = FakeUserServiceTimeBased(user, 29_999)
        val loaded = loadUser(backend)
        assertSame(user, loaded)
    }

    @Test
    fun `testing timeout with a mockk service`() = runBlockingTest {
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
    fun `testing in time with a mockk service`() = runBlockingTest {
        val backend = mockk<UserService>()
        coEvery { backend.load() } coAnswers {
            delay(29_999)
            user
        }
        val loaded = loadUser(backend)
        assertSame(user, loaded)
    }


}


