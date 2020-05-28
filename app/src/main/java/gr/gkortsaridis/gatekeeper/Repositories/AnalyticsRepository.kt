package gr.gkortsaridis.gatekeeper.Repositories

import com.mixpanel.android.mpmetrics.MixpanelAPI
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import org.json.JSONObject

object AnalyticsRepository {

    //Auth Analytics
    const val SIGN_UP = "SIGN UP"
    const val SIGN_IN_PASS = "SIGN IN PASSWORD"
    const val SIGN_IN_BIO = "SIGN IN BIO"
    const val SIGN_IN_PIN = "SIGN IN PIN"
    const val SIGN_IN_ERROR = "SIGN IN ERROR"

    //Content Analytics
    const val LOGINS_LIST = "LOGINS LIST"
    const val CARDS_LIST = "CARDS LIST"
    const val NOTES_LIST = "NOTES LIST"
    const val LOGIN_INFO = "LOGIN INFO"
    const val CARD_INFO = "CARD INFO"
    const val NOTE_INFO = "NOTE INFO"
    const val ACCOUNT_PAGE = "ACCOUNT PAGE"
    const val PASSWORD_GENERATOR = "PASSWORD GENERATOR"
    const val SETTINGS_PAGE = "SETTINGS PAGE"
    const val HISTORY_PAGE = "HISTORY PAGE"
    const val BILLING_PAGE = "BILLING PAGE"
    const val ABOUT_PAGE = "ABOUT PAGE"
    const val LOGOUT = "LOGOUT"

    //Data Analytics
    const val LOGIN_CREATE = "LOGIN CREATE"
    const val LOGIN_CREATE_ERROR = "LOGIN CREATE ERROR"
    const val LOGIN_UPDATE = "LOGIN UPDATE"
    const val LOGIN_UPDATE_ERROR = "LOGIN UPDATE ERROR"
    const val LOGIN_DELETE = "LOGIN DELETE"
    const val LOGIN_DELETE_ERROR = "LOGIN DELETE ERROR"
    const val CARD_CREATE = "CARD CREATE"
    const val CARD_CREATE_ERROR = "CARD CREATE ERROR"
    const val CARD_UPDATE = "CARD UPDATE"
    const val CARD_UPDATE_ERROR = "CARD UPDATE ERROR"
    const val CARD_DELETE = "CARD DELETE"
    const val CARD_DELETE_ERROR = "CARD_DELETE_ERROR"
    const val NOTE_CREATE = "NOTE CREATE"
    const val NOTE_CREATE_ERROR = "NOTE CREATE ERROR"
    const val NOTE_UPDATE = "NOTE UPDATE"
    const val NOTE_UPDATE_ERROR = "NOTE UPDATE ERROR"
    const val NOTE_DELETE = "NOTE DELETE"
    const val NOTE_DELETE_ERROR = "NOTE_DELETE_ERROR"

    //Actions Analytics
    const val LOGIN_PASS_COPY = "LOGIN PASSWORD COPY"
    const val LOGIN_USER_COPY = "LOGIN USERNAME COPY"
    const val ACCOUNT_NAME_CHANGE = "ACCOUNT FULL NAME CHANGE"
    const val ACCOUNT_ICON_CHANGE = "ACCOUNT ICON CHANGE"

    private const val MIXPANEL_TOKEN = "d16de22f59b56298aeac95a6c4458fae"
    private val mixpanel = MixpanelAPI.getInstance(GateKeeperApplication.instance.applicationContext, MIXPANEL_TOKEN)

    fun trackEvent(event: String, props: JSONObject? = null) {
        mixpanel.track(event, props)
    }

    fun identifyUser(userId: String) {
        mixpanel.identify(userId);
        mixpanel.people.identify(userId);
    }

    fun setUserEmail(userEmail: String) {
        val people: MixpanelAPI.People = mixpanel.people
        people["\$email"] = userEmail
        flushEvents()
    }

    fun setUserName(name: String) {
        val people: MixpanelAPI.People = mixpanel.people
        people["\$last_name"] = name
        flushEvents()
    }


    fun flushEvents() {
        mixpanel.flush()
    }
}