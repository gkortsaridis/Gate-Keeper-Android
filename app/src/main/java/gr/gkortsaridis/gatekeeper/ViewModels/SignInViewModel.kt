package gr.gkortsaridis.gatekeeper.ViewModels

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.identifyWith
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.EncryptedData
import gr.gkortsaridis.gatekeeper.Entities.Network.RespUserData
import gr.gkortsaridis.gatekeeper.Entities.UserCredentials
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Repos.AuthenticationRepository
import gr.gkortsaridis.gatekeeper.Repositories.AnalyticsRepository
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import gr.gkortsaridis.gatekeeper.UI.Authentication.LoadingActivity
import gr.gkortsaridis.gatekeeper.Utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository): ViewModel() {
    // ~~~~~~~~~~~~ DATA DISPLAYED IN UI ~~~~~~~~~~~~
    var email: String = ""
    var password: String = ""
    var rememberEmail: Boolean = false


    // ~~~~~~~~~~~~ API CALLS  ~~~~~~~~~~~~
    private val compositeDisposable = CompositeDisposable()
    val signInData = MutableLiveData<Resource<RespUserData>>()

    fun signIn(): LiveData<Resource<RespUserData>> {
        signInData.postValue(Resource.loading(data = null))

        compositeDisposable.add(authenticationRepository.signIn(
            email = email,
            password = password
        ).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if(it.errorCode == -1) {
                        signInData.postValue(Resource.success(data = it.data))
                    } else {
                        signInData.postValue(Resource.error(msg = it.errorMsg, data = null))
                    }
                },
                {
                    signInData.postValue(Resource.error(it.localizedMessage ?: "Unknown Error", data = null))
                }
            )
        )

        return signInData
    }


    // ~~~~~~~~~~~~ REST FUNCTIONALITY  ~~~~~~~~~~~~
    val SIGN_IN_NOT_SET = 0
    val PASSWORD_SIGN_IN = 1
    val BIO_SIN_IN = 2
    val PIN_SIGN_IN = 3

    fun setApplicationUser(userId: String) {
        GateKeeperApplication.user_id = userId
        DataRepository.savedUser = userId

        //Analytics User identity
        AnalyticsRepository.identifyUser(userId)

        //RevenueCat User identity
        Purchases.sharedInstance.identifyWith(userId) { purchaserInfo ->
            // purchaserInfo updated for my_app_user_id
            GateKeeperApplication.purchaserInfo = purchaserInfo
            Log.i("REVENUE CAT PURCHASER INFO", purchaserInfo.toString())
        }
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

    fun isPlusUser(): Boolean {
        return GateKeeperApplication.purchaserInfo?.entitlements?.get("Plus")?.isActive == true
    }

    fun loadCredentials(): UserCredentials? {
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