import kotlinx.coroutines.*
import org.junit.Test
import org.slf4j.LoggerFactory

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