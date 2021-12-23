package gr.gkortsaridis.gatekeeper.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.Network.RespUserData
import gr.gkortsaridis.gatekeeper.Repos.LoginsRepository
import gr.gkortsaridis.gatekeeper.Utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginsRepository: LoginsRepository,
): ViewModel() {

    // ~~~~~~~~~~~~ API CALLS  ~~~~~~~~~~~~

}