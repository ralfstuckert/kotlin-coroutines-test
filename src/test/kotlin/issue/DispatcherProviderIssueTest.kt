package issue

import com.rickbusarow.dispatcherprovider.dispatcherProvider
import com.rickbusarow.dispatcherprovider.test.TestDispatcherProvider
import com.rickbusarow.dispatcherprovider.test.runBlockingTestProvided
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.*

class AdvanceTimeIssueTest {


    @Test
    @ExperimentalCoroutinesApi
    fun advanceTimeIssue() = runBlockingTestProvided {
        delayOnProvidedMain()
        advanceTimeBy(1000)
    }

    fun CoroutineScope.delayOnProvidedMain() {
        launch(dispatcherProvider.main) {
            delay(1000)
        }
    }

    @ExperimentalCoroutinesApi
    private fun contextWithProvider(): CoroutineContext {
        val testDispatcher = TestCoroutineDispatcher()
        val provider = TestDispatcherProvider(testDispatcher)
        return EmptyCoroutineContext + testDispatcher + provider
    }

    @Test
    @ExperimentalCoroutinesApi
    fun advanceTimeIssueFixed() = runBlockingTest(contextWithProvider()) {
        delayOnProvidedMain()
        advanceTimeBy(1000)
    }



    @Test
    @ExperimentalCoroutinesApi
    fun testDelayInLaunchUsingSetMain() = runBlockingTestProvided {
        Dispatchers.setMain(this.coroutineContext[ContinuationInterceptor] as TestCoroutineDispatcher)
        barUsingSetMain()
        advanceTimeBy(1000)
    }

    fun CoroutineScope.barUsingSetMain() {
        launch(Dispatchers.Main) {
            delay(1000)
        }
    }




}