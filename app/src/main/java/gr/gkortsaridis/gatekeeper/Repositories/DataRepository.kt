package gr.gkortsaridis.gatekeeper.Repositories

import android.content.SharedPreferences
import gr.gkortsaridis.gatekeeper.GateKeeperApplication

object DataRepository {

    private const val PREFS_FILENAME = "gatekeeper.prefs"
    private const val CREDENTIALS_EMAIL = "user_credentials_email"
    private const val CREDENTIALS_PASSWORD = "user_credentials_password"
    private const val SAVED_LOGINS = "saved_logins"
    private const val SAVED_CARDS = "saved_cards"
    private const val LAST_ACTIVE_VAULT = "last_active_vault"
    private const val SAVED_USER = "saved_user"
    private const val AUTH_TYPE = "authentication_type"

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

    var savedCards: String?
        get() = prefs.getString(SAVED_CARDS, "")
        set(value) = prefs.edit().putString(SAVED_CARDS, value).apply()

    var savedUser: String?
        get() = prefs.getString(SAVED_USER, "")
        set(value) = prefs.edit().putString(SAVED_USER, value).apply()

    var lastActiveVaultId: String?
        get() = prefs.getString(LAST_ACTIVE_VAULT, "")
        set(value) = prefs.edit().putString(LAST_ACTIVE_VAULT, value).apply()

    var preferredAuthType: Int
        get() = prefs.getInt(AUTH_TYPE, AuthRepository.signInNotSet)
        set(value) = prefs.edit().putInt(AUTH_TYPE, value).apply()
}