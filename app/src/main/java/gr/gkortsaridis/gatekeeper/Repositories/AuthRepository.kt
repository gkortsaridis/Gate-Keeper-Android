package gr.gkortsaridis.gatekeeper.Repositories

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.Entities.Network.ReqBodyUsernameHash
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.SignInListener
import gr.gkortsaridis.gatekeeper.Interfaces.SignUpListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.Authentication.LoadingActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import gr.gkortsaridis.gatekeeper.Utils.pbkdf2_lib
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


object AuthRepository {

    const val SIGN_IN_NOT_SET = 0
    const val PASSWORD_SIGN_IN = 1
    const val BIO_SIN_IN = 2
    const val PIN_SIGN_IN = 3

    private val TAG = "_Auth_Repository_"

    val auth = FirebaseAuth.getInstance()
    val RC_SIGN_IN : Int = 1

    fun signIn(activity:Activity, email: String, password: String, check: Boolean, listener: SignInListener) {
        val hash = pbkdf2_lib.createHash(password = password, username = email)
        val reqBodyUsernameHash = ReqBodyUsernameHash(username = email, hash = password)

        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()

        GateKeeperAPI.api.signIn(reqBodyUsernameHash)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.errorCode != -1) {
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "Email/Password")
                    FirebaseAnalytics.getInstance(activity).logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                    listener.onSignInComplete(it.data.userId)
                } else {
                    listener.onSignInError(it.errorCode, it.errorMsg)
                }
            }

        /*auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                viewDialog.hideDialog()

                if (it.isSuccessful) {
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "Email/Password")
                    FirebaseAnalytics.getInstance(activity).logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                    listener.onSignInComplete(true, FirebaseSignInResult(it.result,null))
                }else {
                    listener.onSignInComplete(false, FirebaseSignInResult(null,it.exception))
                }
            }*/
    }

    fun googleSignIn(activity: Activity) {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(activity, gso)

        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signUp(activity: Activity, email: String, password: String, listener: SignUpListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { result: AuthResult ->
            viewDialog.hideDialog()
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Email/Password")
            FirebaseAnalytics.getInstance(activity).logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
            saveCredentials(email, password)
            listener.onSignUpComplete(true, FirebaseSignInResult(result, null))
            result.user?.sendEmailVerification()
        }.addOnFailureListener {e: java.lang.Exception ->
            viewDialog.hideDialog()
            listener.onSignUpComplete(false, FirebaseSignInResult(null, e))
        }
    }

    fun setApplicationUser(userId: String) {
        GateKeeperApplication.user_id = userId
        DataRepository.savedUser = userId
    }

    fun proceedLoggedIn(activity: Activity) {
        activity.startActivity(Intent(activity, LoadingActivity::class.java))
    }

    fun clearCredentials() {
        DataRepository.userEmail = null
        DataRepository.userPassword = null
    }

    fun saveCredentials(email: String, password: String): Boolean {

        val encryptionEmail = SecurityRepository.encryptWithKeystore(email)
        val encryptionPassword= SecurityRepository.encryptWithKeystore(password)

        return if (encryptionEmail != null && encryptionPassword != null) {
            DataRepository.userEmail = Gson().toJson(encryptionEmail)
            DataRepository.userPassword = Gson().toJson(encryptionPassword)
            true
        }else { false }
    }

    fun loadCredentials():UserCredentials? {
        val encryptedEmail = DataRepository.userEmail
        val encryptedPassword = DataRepository.userPassword

        return if (encryptedEmail != null
            && encryptedEmail != ""
            && encryptedPassword != null
            && encryptedPassword != ""
        ) {
            try {
                val encryptedEmailData = Gson().fromJson(encryptedEmail, EncryptedData::class.java)
                val encryptedPasswordData = Gson().fromJson(encryptedPassword, EncryptedData::class.java)

                val decryptedEmail = SecurityRepository.decryptWithKeystore(encryptedEmailData.encryptedData, encryptedEmailData.iv)
                val decryptedPassword = SecurityRepository.decryptWithKeystore(encryptedPasswordData.encryptedData, encryptedPasswordData.iv)

                UserCredentials(decryptedEmail, decryptedPassword)
            } catch(e: Exception) {
                null
            }

        }else { null }

    }

    fun getUserID (): String {
        return GateKeeperApplication.user_id ?: ""
    }

    fun getPreferredAuthType(): Int{
        return DataRepository.preferredAuthType
    }

}