package api

import api.User
import api.UserService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory


class FakeUserService(val user: User) : UserService {

    val deferredUser = CompletableDeferred<User>()

    fun complete() {
        deferredUser.complete(user)
    }

    override suspend fun load(): User =
        deferredUser.await()

}

class FakeUserServiceTimeBased(val user: User, val delayMs:Long = 1000):
    UserService {

    override suspend fun load(): User {
        delay(delayMs)
        return user
    }

}

val log = LoggerFactory.getLogger("test")
