package api

data class User(val name:String)

interface UserRepo {
    suspend fun store(user: User)
}

interface UserService {
    suspend fun load(): User
}
