package gr.gkortsaridis.gatekeeper.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.AllData
import gr.gkortsaridis.gatekeeper.Repos.LoadingRepository
import gr.gkortsaridis.gatekeeper.Utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val loadingRepository: LoadingRepository
): ViewModel() {

    // ~~~~~~~~~~~~ API CALLS  ~~~~~~~~~~~~
    private val compositeDisposable = CompositeDisposable()
    val allData = MutableLiveData<Resource<AllData>>()

    fun getAllData(userId:String): LiveData<Resource<AllData>> {
        allData.postValue(Resource.loading(data = null))

        compositeDisposable.add(loadingRepository.getAllData(userId).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if(it.errorCode == -1) {
                        allData.postValue(Resource.success(data = it.data))
                    } else {
                        allData.postValue(Resource.error(msg = it.errorMsg ?: "Unknown Error", data = null))
                    }
                },
                {
                    allData.postValue(Resource.error(it.localizedMessage ?: "Unknown Error", data = null))
                }
            )
        )

        return allData
    }
}