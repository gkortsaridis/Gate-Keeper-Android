package gr.gkortsaridis.gatekeeper.Repositories

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Network.ReqBodyUsernameHash
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginDeleteListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginRetrieveListener
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.schedulers.Schedulers
import kotlin.math.log

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

    fun encryptAndStoreLogin(activity: Activity, login: Login, listener: LoginCreateListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()

        GateKeeperAPI.api.createLogin(SecurityRepository.createEncryptedDataRequestBody(login))
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe (
                {
                    viewDialog.hideDialog()
                    if (it.errorCode == -1) { listener.onLoginCreated() }
                    else { listener.onLoginCreateError(it.errorCode, it.errorMsg) }
                },
                {
                    viewDialog.hideDialog()
                    listener.onLoginCreateError(it.hashCode(), it.localizedMessage ?: "")
                }
            )
    }

    fun encryptAndUpdateLogin(activity: Activity, login: Login, listener: LoginCreateListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()

        GateKeeperAPI.api.updateLogin(SecurityRepository.createEncryptedDataRequestBody(login, login.id))
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe (
                {
                    viewDialog.hideDialog()
                    if (it.errorCode == -1) { listener.onLoginCreated() }
                    else { listener.onLoginCreateError(it.errorCode, it.errorMsg) }
                },
                {
                    viewDialog.hideDialog()
                    listener.onLoginCreateError(it.hashCode(), it.localizedMessage ?: "")
                }
            )
    }

    fun filterLoginsByVault(logins: ArrayList<Login>, vault: Vault): ArrayList<Login> {
        val vaultIds = arrayListOf<String>()
        GateKeeperApplication.vaults.forEach { vaultIds.add(it.id) }
        val parentedLogins = ArrayList(logins.filter { vaultIds.contains(it.vault_id) })

        if (vault.id == "-1") { return parentedLogins }

        val filtered = parentedLogins.filter {
            it.vault_id == vault.id
        }

        return ArrayList(filtered)
    }

    fun filterLoginsByCurrentVault(logins: ArrayList<Login>): ArrayList<Login> {
        return filterLoginsByVault(logins, VaultRepository.getLastActiveVault())
    }

    fun getLoginById(loginId: String): Login? {
        for (login in GateKeeperApplication.logins) {
            if (login.id == loginId) return login
        }
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

    fun deleteLogin(login: Login, listener: LoginDeleteListener?) {
        GateKeeperAPI.api.deleteLogin(loginId = login.id, body = SecurityRepository.createUsernameHashRequestBody())
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe (
                {
                    if (it.errorCode == -1 && login.id.toLong() == it.deletedItemID) { listener?.onLoginDeleted() }
                    else { listener?.onLoginDeleteError(it.errorCode, it.errorMsg) }
                },
                {
                    listener?.onLoginDeleteError(it.hashCode(), it.localizedMessage ?: "")
                }
            )
    }

}