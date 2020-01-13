import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test

@UseExperimental(ExperimentalCoroutinesApi::class)
class WithoutRunBlocking {

    @Test
    fun testFooWithAutoProgress() {
        val uncompleted = CompletableDeferred<Int>() // this Deferred<Foo> will never complete

        val scope = TestCoroutineScope()
        scope.foo(uncompleted)
        // foo is suspended waiting for time to progress
        scope.advanceUntilIdle()
        // foo's coroutine will be completed before here
        scope.cleanupTestCoroutines()
    }

    fun CoroutineScope.foo(compl:CompletableDeferred<Int>) {
        launch {
            println(1)            // executes eagerly when foo() is called due to TestCoroutineScope
            delay(1_000)          // suspends until time is advanced by at least 1_000
            compl.await()
            println(2)            // executes after advanceTimeUntilIdle
        }
    }
}