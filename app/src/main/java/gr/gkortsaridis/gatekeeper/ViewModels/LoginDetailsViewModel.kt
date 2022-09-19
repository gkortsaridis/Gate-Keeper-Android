package gr.gkortsaridis.gatekeeper.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.EncryptedDBItem
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Network.RespDeletetItem
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.Repos.UserDataRepository
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import gr.gkortsaridis.gatekeeper.Utils.Resource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class LoginDetailsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): BaseViewModel(userDataRepository = userDataRepository) {

    // ~~~~~~~~~~~~ API CALLS  ~~~~~~~~~~~~
    private val compositeDisposable = CompositeDisposable()
    val createLoginData = MutableLiveData<Resource<Login>>()
    val updateLoginData = MutableLiveData<Resource<Login>>()
    val deleteLoginData = MutableLiveData<Resource<Long>>()

    fun createLogin(login: Login): LiveData<Resource<Login>> {
        createLoginData.postValue(Resource.loading(data = null))
        val body = SecurityRepository.createEncryptedDataRequestBody(login)
        if(body != null) {
            compositeDisposable.add(userDataRepository.createLogin(body = body)
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

    fun updateLogin(login: Login): LiveData<Resource<Login>> {
        updateLoginData.postValue(Resource.loading(data = null))
        val body = SecurityRepository.createEncryptedDataRequestBody(login)
        if(body != null) {
            compositeDisposable.add(userDataRepository.updateLogin(body = body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe (
                    {
                        val decryptedLogin = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(it.data, Login::class.java) as Login?
                        if (decryptedLogin != null) {
                            decryptedLogin.id = it.data.id
                            if (it.errorCode == -1) { updateLoginData.postValue(Resource.success(data = decryptedLogin)) }
                            else { updateLoginData.postValue(Resource.error(msg = it.errorMsg, data = null)) }
                        } else {
                            updateLoginData.postValue(Resource.error(msg = "Decryption Error", data = null))
                        }
                    },
                    {
                        updateLoginData.postValue(Resource.error(msg = it.localizedMessage ?: "", data = null))
                    }
                ))
        }

        return updateLoginData
    }

    fun deleteLogin(login: Login): LiveData<Resource<Long>> {
        val body = SecurityRepository.createUsernameHashRequestBody()
        if(body != null) {
            deleteLoginData.postValue(Resource.loading(data = null))
            compositeDisposable.add(
                userDataRepository.deleteLogin(loginId = login.id, body = body)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.newThread())
                    .subscribe(
                        {
                            if (it.errorCode == -1 && login.id.toLong() == it.deletedItemID) {
                                deleteLoginData.postValue(Resource.success(data = it.deletedItemID))
                            } else {
                                deleteLoginData.postValue(Resource.error(msg = it.errorMsg, data = null))
                            }
                        },
                        {
                            deleteLoginData.postValue(Resource.error(msg = it.localizedMessage ?: "", data = null))
                        }
                    )
            )
        } else {
            deleteLoginData.postValue(Resource.error(msg = "Authentication Error",data = null))
        }

        return deleteLoginData
    }

    // ~~~~~~~~~~~~ HANDLING LOCAL COPIES  ~~~~~~~~~~~~
    fun insertLocalLogin(login: Login){
        val encryptedLogin = SecurityRepository.encryptObjToEncDataWithUserCredentials(login)
        if(encryptedLogin != null) {
            val userData = EncryptedDBItem(
                id = login.id,
                type = 1,
                encryptedData = encryptedLogin.encryptedData,
                iv = encryptedLogin.iv,
                dateCreated = login.date_created,
                dateModified = login.date_modified
            )
            userDataRepository.insertSingleDataObject(userData)
        }
    }

    fun updateLocalLogin(login: Login){
        val encryptedLogin = SecurityRepository.encryptObjToEncDataWithUserCredentials(login)
        if(encryptedLogin != null) {
            val userData = EncryptedDBItem(
                id = login.id,
                type = 1,
                encryptedData = encryptedLogin.encryptedData,
                iv = encryptedLogin.iv,
                dateCreated = login.date_created,
                dateModified = login.date_modified
            )
            userDataRepository.updateSingleDataObject(userData)
        }
    }

    fun deleteLocalLogin(id: String) {
        userDataRepository.deleteSingleLoginObject(id = id)
    }

}