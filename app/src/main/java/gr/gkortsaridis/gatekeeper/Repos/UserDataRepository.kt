package gr.gkortsaridis.gatekeeper.Repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gr.gkortsaridis.gatekeeper.Database.GateKeeperDAO
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.Entities.Network.*
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.Observable
import javax.inject.Inject


class UserDataRepository @Inject constructor(
    private val api: GateKeeperAPI.GateKeeperInterface,
    private val dao: GateKeeperDAO) {

    //~~~~~~~~ DAO TASKS ~~~~~~~~
    fun getLocalVaults(): List<EncryptedDBItem> { return dao.allVaults }
    fun getLocalLogins(): List<EncryptedDBItem> { return dao.allLogins }

    fun getLocalLoginsLive(): LiveData<List<EncryptedDBItem>> { return dao.allLoginsLive }
    fun getLocalVaultsLive(): LiveData<List<EncryptedDBItem>> { return dao.allVaultsLive }
    fun getLocalCardsLive(): LiveData<List<EncryptedDBItem>> { return dao.allCardsLive }

    fun getLocalVaultById(id: String): EncryptedDBItem? { return dao.loadVaultById(id) }
    fun getLocalLoginById(id: String): EncryptedDBItem? { return dao.loadLoginById(id) }

    fun setUserData(dbItems: ArrayList<EncryptedDBItem>) {
        dao.deleteAllData()
        dao.insertUserData(dbItems = dbItems)
    }

    fun insertSingleDataObject(dbItem: EncryptedDBItem) {
        dao.insertSingleDataObject(dbItem = dbItem)
    }

    fun updateSingleDataObject(dbItem: EncryptedDBItem) {
        dao.updateSingleDataObject(dbItem = dbItem)
    }

    fun deleteSingleLoginObject(id: String) {
        dao.deleteLoginById(id = id)
    }

    //~~~~~~~~ API CALLS ~~~~~~~~
    fun getAllData(userId: String): Observable<RespAllData> {
        return api.getAllData(userId = userId)
    }

    fun createLogin(body: ReqBodyEncryptedData): Observable<RespEncryptedData> {
        return api.createLogin(body= body)
    }

    fun updateLogin(body: ReqBodyEncryptedData): Observable<RespEncryptedData> {
        return api.updateLogin(body = body)
    }

    fun deleteLogin(loginId: String,body: ReqBodyUsernameHash): Observable<RespDeletetItem> {
        return api.deleteLogin(loginId = loginId, body= body)
    }

}