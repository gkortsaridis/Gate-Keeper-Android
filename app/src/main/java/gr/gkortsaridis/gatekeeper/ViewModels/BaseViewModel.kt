package gr.gkortsaridis.gatekeeper.ViewModels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.EncryptedDBItem
import gr.gkortsaridis.gatekeeper.Entities.EncryptedData
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Network.ReqBodyEncryptedData
import gr.gkortsaridis.gatekeeper.Entities.Network.RespEncryptedData
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Repos.UserDataRepository
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import io.reactivex.Observable
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel  @Inject constructor(
    private val userDataRepository: UserDataRepository
): ViewModel() {
    //BASIC RETRIEVE/FIND TASKS
    fun getLastActiveVault(): Vault {
        val lastActiveVaultId = DataRepository.lastActiveVaultId ?: ""
        var vaultToReturn = userDataRepository.getAllVaults()[0]
        if (lastActiveVaultId != "") {
            val savedVault = getVaultById(lastActiveVaultId)
            if (savedVault != null) { vaultToReturn = savedVault }
        }
        return vaultToReturn
    }

    fun getVaultById(id: String) = userDataRepository.getVaultById(id)

    val allVaults = userDataRepository.getAllVaults()
    val allLogins = userDataRepository.getAllLogins()

    val allCards  = userDataRepository.getLocalCards()
    val allNotes  = userDataRepository.getLocalNotes()
    val allDevices = userDataRepository.getLocalDevices()

    fun getAllVaultsLive(observer: LifecycleOwner): LiveData<ArrayList<Vault>> {
        val encryptedVaults = userDataRepository.getLocalVaultsLive()

        val decryptedVaults = MutableLiveData<ArrayList<Vault>>()
        decryptedVaults.value = ArrayList()

        encryptedVaults.observe(observer) {
            val vaults = ArrayList<Vault>()
            it.forEach { item ->
                val modifiedVault = EncryptedData(
                    id=item.id,
                    encryptedData = item.encryptedData,
                    iv=item.iv,
                    dateCreated = item.dateCreated,
                    dateModified = item.dateModified
                )
                val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(modifiedVault, Vault::class.java) as Vault?
                if (decrypted != null) {
                    decrypted.id = item.id
                    decrypted.dateCreated = item.dateCreated
                    decrypted.dateModified = item.dateModified
                    vaults.add(decrypted)
                }
            }
            decryptedVaults.value = vaults
        }

        return decryptedVaults
    }

    fun getAllLoginsLive(observer: LifecycleOwner): LiveData<ArrayList<Login>> {
        val encryptedLogins = userDataRepository.getLocalLoginsLive()

        val decryptedLogins = MutableLiveData<ArrayList<Login>>()
        decryptedLogins.value = ArrayList()

        encryptedLogins.observe(observer) {
            val logins = ArrayList<Login>()
            it.forEach { item ->
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
            decryptedLogins.value = logins
        }

        return decryptedLogins
    }

    fun getLoginById(id: String?): Login? {
        if(id == null) { return null }

        val encLogin = userDataRepository.getLocalLoginById(id)
        if(encLogin != null) {
            val modifiedLogin = EncryptedData(
                id=encLogin.id,
                encryptedData = encLogin.encryptedData,
                iv=encLogin.iv,
                dateCreated = encLogin.dateCreated,
                dateModified = encLogin.dateModified
            )
            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(modifiedLogin, Login::class.java) as Login?
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
}