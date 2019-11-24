package gr.gkortsaridis.gatekeeper.Repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginRetrieveListener
import java.util.concurrent.CompletableFuture

object LoginsRepository {

    fun encryptAndStoreLogin(login: Login, listener: LoginCreateListener) {
        val encryptedLogin = login.encrypt()

        val loginhash = hashMapOf(
            "login" to encryptedLogin,
            "account_id" to GateKeeperApplication.user.uid
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("logins")
            .add(loginhash)
            .addOnCompleteListener {
                if (it.isSuccessful) { listener.onLoginCreated() }
                else { listener.onLoginCreateError() }
            }
    }

    fun retrieveLoginsByAccountID(accountID: String, retrieveListener: LoginRetrieveListener) {

        val db = FirebaseFirestore.getInstance()
        db.collection("logins")
            .whereEqualTo("account_id",accountID)
            .get().addOnSuccessListener { result ->
                val loginsResult = ArrayList<Login>()
                for (document in result) {
                    val encryptedLogin = document["login"] as String
                    loginsResult.add(decryptLogin(encryptedLogin))
                }

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

}