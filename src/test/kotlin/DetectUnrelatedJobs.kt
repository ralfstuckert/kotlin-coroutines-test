import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.startsWith
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.lang.IllegalStateException

/**
 * [runBlockingTest] counts active jobs before and after execution of
 * the test block, and raises an exception in case of a mismatch. The
 * problem is usually based in an unintential use of a non-test dispatcher.
 */
@UseExperimental(ExperimentalCoroutinesApi::class)
class DetectUnrelatedJobs {

    /**
     * GlobalScope has its own dispatcher, therefore all (time-)control
     * provided by the TestCoroutineDispatcher is lost. [runBlockingTest]
     * will detect this and complain.
     */
    @Test
    fun `runBlockingTest() detects uncompleted Jobs`() {
        val ex = assertThrows<IllegalStateException> {
            runBlockingTest() {
                val job = GlobalScope.launch() {
                    delay(2000)
                }
                job.join()
                println("We will never see this")
            }
        }
        assertThat(ex.message ?: "", startsWith("This job has not completed yet"))
    }


    /**
     * GlobalScope has its own dispatcher, therefore all (time-)control
     * provided by the TestCoroutineDispatcher is lost. [runBlockingTest]
     * will detect this and complain.
     */
    @Test
    fun `runBlockingTest() detects using non-TestDispatcher`() {
        val ex = assertThrows<IllegalStateException> {
            runBlockingTest() {
                val value = async(Dispatchers.IO) {
                    delay(2000)
                    17
                }.await()
                println("We will never see value ${value}")
            }
        }
        assertThat(ex.message ?: "", startsWith("This job has not completed yet"))
    }


}