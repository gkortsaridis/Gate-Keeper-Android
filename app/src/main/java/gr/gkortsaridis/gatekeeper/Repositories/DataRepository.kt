package gr.gkortsaridis.gatekeeper.Repositories

import android.content.SharedPreferences
import gr.gkortsaridis.gatekeeper.GateKeeperApplication

object DataRepository {

    private val PREFS_FILENAME = "gatekeeper.prefs"
    private val CREDENTIALS_EMAIL = "user_credentials_email"
    private val CREDENTIALS_PASSWORD = "user_credentials_password"
    private val prefs: SharedPreferences = GateKeeperApplication.instance.getSharedPreferences(PREFS_FILENAME, 0)

    var userEmail: String?
        get() = prefs.getString(CREDENTIALS_EMAIL, "")
        set(value) = prefs.edit().putString(CREDENTIALS_EMAIL, value).apply()

    var userPassword: String?
        get() = prefs.getString(CREDENTIALS_PASSWORD, "")
        set(value) = prefs.edit().putString(CREDENTIALS_PASSWORD, value).apply()
}