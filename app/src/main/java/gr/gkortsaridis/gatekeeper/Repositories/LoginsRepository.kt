package gr.gkortsaridis.gatekeeper.Repositories

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginDeleteListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginRetrieveListener

object LoginsRepository {

    val createLoginRequestCode = 1
    val createLoginSuccess = 2
    val createLoginError = 3
    val deleteLoginSuccess = 4

    fun encryptAndStoreLogin(activity: Activity, login: Login, listener: LoginCreateListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()

        val encryptedLogin = SecurityRepository.encryptObjectWithUserCredentials(login)

        val loginhash = hashMapOf(
            "login" to encryptedLogin,
            "account_id" to AuthRepository.getUserID()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("logins")
            .add(loginhash)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    viewDialog.hideDialog()
                    listener.onLoginCreated()
                }
                else {
                    viewDialog.hideDialog()
                    listener.onLoginCreateError()
                }
            }
    }

    fun encryptAndUpdateLogin(activity: Activity, login: Login, listener: LoginCreateListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()

        val encryptedLogin = SecurityRepository.encryptObjectWithUserCredentials(login)

        val loginhash = hashMapOf(
            "login" to encryptedLogin,
            "account_id" to AuthRepository.getUserID()
        )

        val db = FirebaseFirestore.getInstance()
        db.document("logins/"+login.id)
            .update(loginhash as Map<String, Any>)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    viewDialog.hideDialog()
                    listener.onLoginCreated()
                }
                else {
                    viewDialog.hideDialog()
                    listener.onLoginCreateError()
                }
            }
    }

    fun retrieveLoginsByAccountID(accountID: String, retrieveListener: LoginRetrieveListener) {

        val db = FirebaseFirestore.getInstance()
        db.collection("logins")
            .whereEqualTo("account_id",accountID)
            .get().addOnSuccessListener { result ->
                val loginsResult = ArrayList<Login>()

                val encryptedLoginsToSaveLocally = ArrayList<String>()

                for (document in result) {
                    val encryptedLogin = document["login"] as String
                    encryptedLoginsToSaveLocally.add(encryptedLogin)
                    val decryptedLogin = SecurityRepository.decryptStringToObjectWithUserCredentials(encryptedLogin, Login::class.java) as Login?
                    if (decryptedLogin != null){
                        decryptedLogin.id = document.id
                        loginsResult.add(decryptedLogin)
                    }
                }

                //Save logins locally
                DataRepository.savedLogins = Gson().toJson(encryptedLoginsToSaveLocally)

                retrieveListener.onLoginsRetrieveSuccess(loginsResult)
            }
            .addOnFailureListener { exception -> retrieveListener.onLoginsRetrieveError(exception) }
    }

    fun filterLoginsByVault(logins: ArrayList<Login>, vault: Vault): ArrayList<Login> {
        if (vault.id == "-1") { return logins }

        val filtered = logins.filter {
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

        val savedLogins = Gson().fromJson<ArrayList<String>>(savedLoginsStr, turnsType)

        val decryptedLogins = ArrayList<Login>()

        for (encryptedLogin in savedLogins) {
            val decryptedLogin = SecurityRepository.decryptStringToObjectWithUserCredentials(encryptedLogin, Login::class.java) as Login?
            if (decryptedLogin != null) {
                decryptedLogins.add(decryptedLogin)
            }

        }

        return decryptedLogins
    }

    fun deleteLogin(login: Login, listener: LoginDeleteListener?) {
        val db = FirebaseFirestore.getInstance()
        db.collection("logins")
            .document(login.id)
            .delete()
            .addOnCompleteListener {
                listener?.onLoginDeleted()
            }

    }

}