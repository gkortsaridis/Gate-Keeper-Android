package gr.gkortsaridis.gatekeeper.Repositories

import android.content.SharedPreferences
import gr.gkortsaridis.gatekeeper.GateKeeperApplication

object DataRepository {

    private const val PREFS_FILENAME = "gatekeeper.prefs"
    private const val CREDENTIALS_EMAIL = "user_credentials_email"
    private const val CREDENTIALS_PASSWORD = "user_credentials_password"
    private const val SAVED_LOGINS = "saved_logins"
    private const val SAVED_USER = "saved_user"

    private val prefs: SharedPreferences = GateKeeperApplication.instance.getSharedPreferences(PREFS_FILENAME, 0)

    var userEmail: String?
        get() = prefs.getString(CREDENTIALS_EMAIL, "")
        set(value) = prefs.edit().putString(CREDENTIALS_EMAIL, value).apply()

    var userPassword: String?
        get() = prefs.getString(CREDENTIALS_PASSWORD, "")
        set(value) = prefs.edit().putString(CREDENTIALS_PASSWORD, value).apply()

    var savedLogins: String?
        get() = prefs.getString(SAVED_LOGINS,"")
        set(value) = prefs.edit().putString(SAVED_LOGINS, value).apply()

    var savedUser: String?
        get() = prefs.getString(SAVED_USER, "")
        set(value) = prefs.edit().putString(SAVED_USER, value).apply()
}