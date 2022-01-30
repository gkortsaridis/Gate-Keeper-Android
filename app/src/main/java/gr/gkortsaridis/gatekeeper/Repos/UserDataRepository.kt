package gr.gkortsaridis.gatekeeper.Repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gr.gkortsaridis.gatekeeper.Database.GateKeeperDAO
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.Entities.Network.ReqBodyEncryptedData
import gr.gkortsaridis.gatekeeper.Entities.Network.RespAllData
import gr.gkortsaridis.gatekeeper.Entities.Network.RespEncryptedData
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.Observable
import javax.inject.Inject


class UserDataRepository @Inject constructor(
    private val api: GateKeeperAPI.GateKeeperInterface,
    private val dao: GateKeeperDAO) {

    //DAO TASKS
    fun insertSingleDataObject(dbItem: EncryptedDBItem) {
        dao.insertSingleDataObject(dbItem = dbItem)
    }

    //Low Level - Encrypted Data
    fun getLocalVaults(): List<EncryptedDBItem> { return dao.allVaults }
    fun getLocalLogins(): List<EncryptedDBItem> { return dao.allLogins }

    fun getLocalLoginsLive(): LiveData<List<EncryptedDBItem>> { return dao.allLoginsLive }
    fun getLocalVaultsLive(): LiveData<List<EncryptedDBItem>> { return dao.allVaultsLive }

    fun getLocalVaultById(id: String): EncryptedDBItem? { return dao.loadVaultById(id) }
    fun getLocalLoginById(id: String): EncryptedDBItem? { return dao.loadLoginById(id) }

    fun setUserData(dbItems: ArrayList<EncryptedDBItem>) {
        dao.deleteAllData()
        dao.insertUserData(dbItems = dbItems)
    }

    // API CALLS
    fun getAllData(userId: String): Observable<RespAllData> {
        return api.getAllData(userId = userId)
    }

    fun createLogin(body: ReqBodyEncryptedData): Observable<RespEncryptedData> {
        return api.createLogin(body= body)
    }

    fun updateLogin(body: ReqBodyEncryptedData): Observable<RespEncryptedData> {
        return api.updateLogin(body = body)
    }

}