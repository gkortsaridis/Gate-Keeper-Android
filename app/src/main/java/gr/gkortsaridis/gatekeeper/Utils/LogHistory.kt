package gr.gkortsaridis.gatekeeper.Utils

object LogHistory {
    fun getFormattedLog(log: String): String {
        return when (log) {
            "SIGN_IN_SUCCESS" -> "Sign In Success"
            "SIGN_UP_SUCCESS" -> "Sign Up Success"
            "SIGN_IN_FAILURE" -> "Sign In Failure"
            "LOGIN_CREATE" -> "Password Create"
            "LOGIN_UPDATE" -> "Password Update"
            "LOGIN_DELETE" -> "Password Delete"
            "CARD_CREATE" -> "Credit Card Create"
            "CARD_UPDATE" -> "Credit Card Update"
            "CARD_DELETE" -> "Credit Card Delete"
            "NOTE_CREATE" -> "Note Create"
            "NOTE_UPDATE" -> "Note Update"
            "NOTE_DELETE" -> "Note Delete"
            "USER_DATA_UPDATE" -> "User Data Update"
            else -> log
        }
    }
}