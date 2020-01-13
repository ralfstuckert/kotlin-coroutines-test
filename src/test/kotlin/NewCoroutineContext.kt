import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import kotlin.test.*

@UseExperimental(ExperimentalCoroutinesApi::class)
class NewCoroutineContext {

    val log = LoggerFactory.getLogger(NewCoroutineContext::class.java)

    @Test
    fun doesWithContextCreateNewCoroutine():Unit = runBlocking {
        log.info("start")
        withContext(Dispatchers.IO) {
            log.info("in withContext")
        }
        launch {
            throw CancellationException("hihi")

        }
        log.info("end")
    }


}