package gr.gkortsaridis.gatekeeper.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Repos.UserDataRepository
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import gr.gkortsaridis.gatekeeper.Utils.Resource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


@HiltViewModel
class LoginDetailsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): ViewModel() {

    fun getLastActiveVault() = userDataRepository.getLastActiveVault()
    fun getLoginById(id: String?) = userDataRepository.getLoginById(id)
    fun insertLocalLogin(login: Login) = userDataRepository.insertLocalLogin(login)

    // ~~~~~~~~~~~~ API CALLS  ~~~~~~~~~~~~
    private val compositeDisposable = CompositeDisposable()
    val createLoginData = MutableLiveData<Resource<Login>>()


    fun createLogin(login: Login): LiveData<Resource<Login>> {
        createLoginData.postValue(Resource.loading(data = null))
        val body = SecurityRepository.createEncryptedDataRequestBody(login)
        if(body != null) {
            compositeDisposable.add(userDataRepository.createLogin(body = body!!)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe (
                    {
                        val decryptedLogin = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(it.data, Login::class.java) as Login?
                        if (decryptedLogin != null) {
                            decryptedLogin.id = it.data.id.toString()
                            if (it.errorCode == -1) {
                                createLoginData.postValue(Resource.success(data = decryptedLogin))
                            }
                            else {
                                createLoginData.postValue(Resource.error(msg = it.errorMsg, data = null))
                            }
                        } else {
                            createLoginData.postValue(Resource.error(msg = "Decryption Error", data = null))
                        }
                    },
                    {
                        createLoginData.postValue(Resource.error(msg = it.localizedMessage, data = null))
                    }
                ))
        } else {
            createLoginData.postValue(Resource.error(msg = "Encryption Error", data = null))
        }


        return createLoginData
    }

}