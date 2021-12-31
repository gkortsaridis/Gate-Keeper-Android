package gr.gkortsaridis.gatekeeper.Repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gr.gkortsaridis.gatekeeper.Database.GateKeeperDAO
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.Entities.Network.RespAllData
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.Observable
import javax.inject.Inject


class UserDataRepository @Inject constructor(
    private val api: GateKeeperAPI.GateKeeperInterface,
    private val dao: GateKeeperDAO) {

    //High Level - Decrypted Data
    fun getAllVaults(): ArrayList<Vault> {
        val vaults = ArrayList<Vault>()
        val encryptedVaults = getLocalVaults()
        encryptedVaults.forEach { vault ->
            val modifiedVault = EncryptedData(
                id=vault.id,
                encryptedData = vault.encryptedData,
                iv=vault.iv,
                dateCreated = vault.dateCreated,
                dateModified = vault.dateModified
            )
            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(modifiedVault, Vault::class.java) as Vault?
            if (decrypted != null) {
                decrypted.id = vault.id
                decrypted.dateCreated = vault.dateCreated
                decrypted.dateModified = vault.dateModified
                vaults.add(decrypted)
            }
        }

        return vaults
    }
    fun getAllLogins(): ArrayList<Login> {
        val logins = ArrayList<Login>()
        val encryptedLogins = getLocalLogins()
        encryptedLogins.forEach { item ->
            val modifiedItem = EncryptedData(
                id=item.id,
                encryptedData = item.encryptedData,
                iv=item.iv,
                dateCreated = item.dateCreated,
                dateModified = item.dateModified
            )
            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(modifiedItem, Login::class.java) as Login?
            if (decrypted != null) {
                decrypted.id = item.id
                decrypted.date_created = item.dateCreated
                decrypted.date_modified = item.dateModified
                logins.add(decrypted)
            }
        }

        return logins
    }

    fun getVaultById(id: String): Vault? {
        val encVault = getLocalVaultById(id)
        if(encVault != null) {
            val modifiedVault = EncryptedData(
                id=encVault.id,
                encryptedData = encVault.encryptedData,
                iv=encVault.iv,
                dateCreated = encVault.dateCreated,
                dateModified = encVault.dateModified
            )
            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(modifiedVault, Vault::class.java) as Vault?
            if (decrypted != null) {
                decrypted.id = encVault.id
                decrypted.dateCreated = encVault.dateCreated
                decrypted.dateModified = encVault.dateModified
            }

            return decrypted
        } else {
            return null
        }
    }

    fun getLoginById(id: String): Login? {
        val encLogin = getLocalLoginById(id)
        if(encLogin != null) {
            val modifiedLogin = EncryptedData(
                id=encLogin.id,
                encryptedData = encLogin.encryptedData,
                iv=encLogin.iv,
                dateCreated = encLogin.dateCreated,
                dateModified = encLogin.dateModified
            )
            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(modifiedLogin, Vault::class.java) as Login?
            if (decrypted != null) {
                decrypted.id = encLogin.id
                decrypted.date_created = encLogin.dateCreated
                decrypted.date_modified = encLogin.dateModified
            }

            return decrypted
        } else {
            return null
        }
    }

    fun getLastActiveVault(): Vault {
        val lastActiveVaultId = DataRepository.lastActiveVaultId ?: ""
        var vaultToReturn = getAllVaults()[0]
        //if (lastActiveVaultId != "") {
        //    val savedVault = VaultRepository.getVaultByID(lastActiveVaultId)
        //    if (savedVault != null) { vaultToReturn = savedVault}
        //}

        return vaultToReturn
    }

    //Low Level - Encrypted Data
    private fun getLocalVaults(): List<EncryptedDBItem> { return dao.allVaults }
    private fun getLocalLogins(): List<EncryptedDBItem> { return dao.allLogins }
    private fun getLocalVaultById(id: String): EncryptedDBItem? { return dao.loadVaultById(id) }
    private fun getLocalLoginById(id: String): EncryptedDBItem? { return dao.loadLoginById(id) }

    fun getLocalCards():  MutableLiveData<ArrayList<CreditCard>> { return GateKeeperApplication.allCards }
    fun getLocalNotes(): MutableLiveData<ArrayList<Note>> { return GateKeeperApplication.allNotes }
    fun getLocalDevices(): MutableLiveData<ArrayList<Device>> { return GateKeeperApplication.allDevices }

    fun setUserData(dbItems: ArrayList<EncryptedDBItem>) {
        dao.deleteAllData()
        dao.insertUserData(dbItems = dbItems)
    }

    // API CALLS
    fun getAllData(userId: String): Observable<RespAllData> {
        return api.getAllData(userId = userId)
    }


}