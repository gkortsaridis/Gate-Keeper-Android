package gr.gkortsaridis.gatekeeper.Repositories

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginRetrieveListener
import java.util.concurrent.CompletableFuture

object LoginsRepository {

    val createLoginRequestCode = 1
    val createLoginSuccess = 2
    val createLoginError = 3

    fun encryptAndStoreLogin(activity: Activity, login: Login, listener: LoginCreateListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()

        val encryptedLogin = login.encrypt()

        val loginhash = hashMapOf(
            "login" to encryptedLogin,
            "account_id" to GateKeeperApplication.user.uid
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

        val encryptedLogin = login.encrypt()

        val loginhash = hashMapOf(
            "login" to encryptedLogin,
            "account_id" to GateKeeperApplication.user.uid
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
                    val decryptedLogin = decryptLogin(encryptedLogin)
                    decryptedLogin.id = document.id
                    loginsResult.add(decryptedLogin)
                }

                //Save logins locally
                DataRepository.savedLogins = Gson().toJson(encryptedLoginsToSaveLocally)

                retrieveListener.onLoginsRetrieveSuccess(loginsResult)
            }
            .addOnFailureListener { exception -> retrieveListener.onLoginsRetrieveError(exception) }
    }

    fun decryptLogin(encryptedLogin: String) : Login {

        val response = CompletableFuture<Login>()
        ECSymmetric().decrypt(encryptedLogin, GateKeeperApplication.user.uid, object :
            ECResultListener {
            override fun onFailure(message: String, e: Exception) {
                response.complete(null)
            }

            override fun <T> onSuccess(result: T) {
                response.complete(Gson().fromJson(result.toString(), Login::class.java))
            }
        })

        return response.get()
    }

    fun filterLoginsByCurrentVaultAndFolder(logins: ArrayList<Login>): ArrayList<Login> {
        val filtered = logins.filter {
            it.vault_id == GateKeeperApplication.activeVault.id
        }

        return ArrayList(filtered)
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

}