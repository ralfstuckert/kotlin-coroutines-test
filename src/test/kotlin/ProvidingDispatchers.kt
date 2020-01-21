import api.User
import api.UserService
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.startsWith
import com.rickbusarow.dispatcherprovider.DispatcherProvider
import com.rickbusarow.dispatcherprovider.dispatcherProvider
import com.rickbusarow.dispatcherprovider.test.TestDispatcherProvider
import com.rickbusarow.dispatcherprovider.test.runBlockingTestProvided
import com.rickbusarow.dispatcherprovider.withIO
import coroutines.testDispatcher
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.coroutines.coroutineContext

suspend fun loadUserProvidedDispatcher(backend: UserService, dispatcherProvider: DispatcherProvider): User =
    withContext(dispatcherProvider.io) {
        backend.load()
    }

suspend fun loadUserContextIO(backend: UserService): User =
    withContext(coroutineContext.dispatcherProvider.io) {
        backend.load()
    }

suspend fun loadUserWithIO(backend: UserService): User =
    withIO {
        backend.load()
    }

/**
 * In future releases it might be possible to test code directly using [Dispatchers.IO] etc,
 * see issue [1365](https://github.com/Kotlin/kotlinx.coroutines/issues/1365).
 */
@ExperimentalCoroutinesApi
class ProvidingDispatchers {

    private val backend: UserService = mockk()
    private val user = User("Herbert")


    @BeforeEach
    fun setUp() {
        clearAllMocks()

        coEvery { backend.load() } coAnswers {
            // delay in order to check auto-advance of TestDispatcher
            delay(30_000)
            user
        }
    }

    @Test
    fun `inject or pass DispatcherProvider`() = runBlockingTest {
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        val loaded = loadUserProvidedDispatcher(backend, dispatcherProvider)
        assertSame(user, loaded)
    }

    @Test
    fun `pass DispatcherProvider via coroutine context`() = runBlockingTestProvided {
        val loaded = loadUserContextIO(backend)
        assertSame(user, loaded)
    }

    @Test
    fun `convenience function withIO()`() = runBlockingTestProvided {
        val loaded = loadUserWithIO(backend)
        assertSame(user, loaded)
    }

    @Test
    fun `make sure using runBlockingTestProvided()`() {
        val ex = assertThrows<IllegalStateException> {

            runBlockingTest {
                val loaded = loadUserWithIO(backend)
                assertSame(user, loaded)
            }
        }
        // test was run using the DefaultDispatcherProvider and therefore
        // not using TestCoroutineDispatcher, but the real IO dispatcher.
        // So auto-advance fails...
        assertThat(ex.message ?: "", startsWith("This job has not completed yet"))
    }
}