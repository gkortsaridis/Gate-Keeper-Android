package gr.gkortsaridis.gatekeeper.Repos

import androidx.lifecycle.MutableLiveData
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.Entities.Network.RespAllData
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.Observable
import javax.inject.Inject


class UserDataRepository @Inject constructor(
    private val api: GateKeeperAPI.GateKeeperInterface
    ) {

    //LOCAL DATA
    fun getLocalVaults(): MutableLiveData<ArrayList<Vault>> {
        return GateKeeperApplication.allVaults
    }
    fun getLocalLogins(): MutableLiveData<ArrayList<Login>> { return GateKeeperApplication.allLogins }
    fun getLocalCards():  MutableLiveData<ArrayList<CreditCard>> { return GateKeeperApplication.allCards }
    fun getLocalNotes(): MutableLiveData<ArrayList<Note>> { return GateKeeperApplication.allNotes }
    fun getLocalDevices(): MutableLiveData<ArrayList<Device>> { return GateKeeperApplication.allDevices }

    fun updateVaults(vaults: ArrayList<Vault>) {
        GateKeeperApplication.allVaults.postValue(vaults)
    }

    fun updateLogins(logins: ArrayList<Login>) {
        GateKeeperApplication.allLogins.postValue(logins)
    }

    fun updateCards(cards: ArrayList<CreditCard>) {
        GateKeeperApplication.allCards.postValue(cards)
    }

    fun updateNotes(notes: ArrayList<Note>) {
        GateKeeperApplication.allNotes.postValue(notes)
    }

    fun updateDevices(devices: ArrayList<Device>) {
        GateKeeperApplication.allDevices.postValue(devices)
    }

    // API CALLS
    fun getAllData(userId: String): Observable<RespAllData> {
        return api.getAllData(userId = userId)
    }


}