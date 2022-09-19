package gr.gkortsaridis.gatekeeper.Repositories

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gr.gkortsaridis.gatekeeper.Database.GatekeeperDatabase
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginDeleteListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginUpdateListener
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
object LoginsRepository {

    const val LOGIN_CLICK_ACTION_COPY = 1
    const val LOGIN_CLICK_ACTION_OPEN = 2

    const val LOGIN_SORT_TYPE_NAME = 0
    const val LOGIN_SORT_TYPE_DATE_EDITED = 1

    val createLoginRequestCode = 1
    val createLoginSuccess = 2
    val createLoginError = 3
    val deleteLoginSuccess = 4
    val deleteLoginError = 5

    fun getLoginById(loginId: String): Login? {
    //    return db.dao().loadLoginById(loginId)
        return null
    }

    fun getApplicationInfoByPackageName(packageName: String?, packageManager: PackageManager): ResolveInfo? {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pkgAppsList: List<ResolveInfo> = packageManager.queryIntentActivities(mainIntent, 0)
        for (app in pkgAppsList) {
            if (app.activityInfo.packageName == packageName) {
                return app
            }
        }
        return null
    }

    fun getSavedLogins(): ArrayList<Login> {

        val savedLoginsStr = DataRepository.savedLogins
        val turnsType = object : TypeToken<ArrayList<String>>() {}.type
        val decryptedLogins = ArrayList<Login>()
        val savedLogins = Gson().fromJson<ArrayList<String>>(savedLoginsStr, turnsType)

        if (savedLogins != null) {
            for (encryptedLogin in savedLogins) {
                val decryptedLogin = SecurityRepository.decryptStringToObjectWithUserCredentials(encryptedLogin, Login::class.java) as Login?
                if (decryptedLogin != null) {
                    decryptedLogins.add(decryptedLogin)
                }

            }
        }

        return decryptedLogins
    }

}