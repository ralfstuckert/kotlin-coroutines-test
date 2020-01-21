package coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.*

@ExperimentalCoroutinesApi
class MainDispatcherExtension : ParameterResolver, BeforeEachCallback, AfterEachCallback {

    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ) =
        parameterContext.parameter.type.isAssignableFrom(TestCoroutineDispatcher::class.java)

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? =
        extensionContext.testDispatcher

    override fun beforeEach(extensionContext: ExtensionContext) {
        val dispatcher = TestCoroutineDispatcher()
        extensionContext.testDispatcher = dispatcher
        Dispatchers.setMain(dispatcher)
    }

    override fun afterEach(extensionContext: ExtensionContext) {
        Dispatchers.resetMain()
        extensionContext.testDispatcher?.cleanupTestCoroutines()
    }


}

private const val DISPATCHER = "TestCoroutineDispatcher"

@ExperimentalCoroutinesApi
private var ExtensionContext.testDispatcher: TestCoroutineDispatcher?
    get() = store[DISPATCHER] as? TestCoroutineDispatcher
    set(value) {
        store.put(DISPATCHER, value)
    }

@ExperimentalCoroutinesApi
private val ExtensionContext.store
    get() =
        this.getStore(
            ExtensionContext.Namespace.create(
                MainDispatcherExtension::class.java,
                this.requiredTestMethod
            )
        )

