package gr.gkortsaridis.gatekeeper.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.Network.RespUserData
import gr.gkortsaridis.gatekeeper.Repos.AuthenticationRepository
import gr.gkortsaridis.gatekeeper.Utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository): ViewModel() {
    var email: String = ""
    var password: String = ""
    var rememberEmail: Boolean = false

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

}