import com.rickbusarow.dispatcherprovider.DispatcherProvider
import com.rickbusarow.dispatcherprovider.dispatcherProvider
import com.rickbusarow.dispatcherprovider.test.TestDispatcherProvider
import com.rickbusarow.dispatcherprovider.test.runBlockingTestProvided
import com.rickbusarow.dispatcherprovider.withIO
import coroutines.testDispatcher
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import kotlin.coroutines.coroutineContext
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertSame

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

    @MockK
    private lateinit var backend: UserService
    private val user = User("Herbert")


    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery {backend.load() } coAnswers {
            // delay in order to check auto-advance of TestDispatcher
            delay(30_000)
            user
        }
    }

    @Test
    fun explicitDispatcherProvider() = runBlockingTest {
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        val loaded = loadUserProvidedDispatcher(backend, dispatcherProvider)
        assertSame(user, loaded)
    }

    @Test
    fun implicitDispatcherProvider() = runBlockingTestProvided {
        val loaded = loadUserContextIO(backend)
        assertSame(user, loaded)
    }

    @Test
    fun usingWithIO() = runBlockingTestProvided {
        val loaded = loadUserWithIO(backend)
        assertSame(user, loaded)
    }

    @Test(expected = IllegalStateException::class)
    fun failsWithDefaultProvider() = runBlockingTest {
        val loaded = loadUserWithIO(backend)
        assertSame(user, loaded)
    }

}