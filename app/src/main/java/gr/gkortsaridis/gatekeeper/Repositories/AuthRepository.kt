package gr.gkortsaridis.gatekeeper.Repositories

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.Entities.Network.ReqBodySignUp
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


@SuppressLint("CheckResult")
object AuthRepository {

    const val SIGN_IN_NOT_SET = 0
    const val PASSWORD_SIGN_IN = 1
    const val BIO_SIN_IN = 2
    const val PIN_SIGN_IN = 3

    private val TAG = "_Auth_Repository_"

    fun signIn(activity: Activity, email: String, password: String, listener: SignInListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()

        val hash = pbkdf2_lib.createHash(password, email)
        val device = SecurityRepository.encryptObjectWithUserCreds(DeviceRepository.getCurrentDevice(GateKeeperApplication.instance))
        val body = ReqBodyUsernameHash(
            username = email,
            hash = hash,
            deviceEncryptedData = device!!.encryptedData,
            deviceIv = device.iv)

        GateKeeperAPI.api.signIn(body)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    viewDialog.hideDialog()

                    if (it.errorCode == -1) {
                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Email/Password")
                        FirebaseAnalytics.getInstance(activity).logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                        listener.onSignInComplete(it.data.userId)
                    } else {
                        listener.onSignInError(it.errorCode, it.errorMsg)
                    }
                },
                {
                    viewDialog.hideDialog()
                    listener.onSignInError(it.hashCode(), it.localizedMessage ?: "")
                }
            )
    }

    fun signUp(activity: Activity, email: String, password: String, listener: SignUpListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()

        val hash = pbkdf2_lib.createHash(password = password, username = email)
        val reqBodyUsernameHash = ReqBodySignUp(username = email, hash = hash, name = "")
        GateKeeperAPI.api.signUp(reqBodyUsernameHash)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    viewDialog.hideDialog()
                    if (it.errorCode == -1) {
                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Email/Password")
                        FirebaseAnalytics.getInstance(activity).logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
                        listener.onSignUpComplete(it.data.userId)
                    } else {
                        listener.onSignUpError(it.errorCode, it.errorMsg)
                    }
                },
                {
                    viewDialog.hideDialog()
                    listener.onSignUpError(it.hashCode(), it.localizedMessage ?: "")
                }
            )
    }

    fun setApplicationUser(userId: String) {
        GateKeeperApplication.user_id = userId
        DataRepository.savedUser = userId
    }

    fun proceedLoggedIn(activity: Activity) {
        activity.startActivity(Intent(activity, LoadingActivity::class.java))
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