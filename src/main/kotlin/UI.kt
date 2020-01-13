
sealed class Confirmation {
    object OK:Confirmation()
    object Cancel:Confirmation()
}

interface UI {
    suspend fun waitForUserConfirm(message:String):Confirmation
}