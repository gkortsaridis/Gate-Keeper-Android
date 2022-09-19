package gr.gkortsaridis.gatekeeper.ViewModels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.Entities.Network.ReqBodyEncryptedData
import gr.gkortsaridis.gatekeeper.Entities.Network.RespEncryptedData
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
        var vaultToReturn = getAllVaults()[0]
        if (lastActiveVaultId != "") {
            val savedVault = getVaultById(lastActiveVaultId)
            if (savedVault != null) { vaultToReturn = savedVault }
        }
        return vaultToReturn
    }

    fun getAllVaults(): ArrayList<Vault> {
        val vaults = ArrayList<Vault>()
        val encryptedVaults = userDataRepository.getLocalVaults()
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
        val encryptedLogins = userDataRepository.getLocalLogins()
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

    fun getAllCardsLive(observer: LifecycleOwner): LiveData<ArrayList<CreditCard>> {
        val encryptedCards = userDataRepository.getLocalCardsLive()

        val decryptedCards = MutableLiveData<ArrayList<CreditCard>>()
        decryptedCards.value = ArrayList()

        encryptedCards.observe(observer) {
            val cards = ArrayList<CreditCard>()
            it.forEach { item ->
                val modifiedVault = EncryptedData(
                    id=item.id,
                    encryptedData = item.encryptedData,
                    iv=item.iv,
                    dateCreated = item.dateCreated,
                    dateModified = item.dateModified
                )
                val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(modifiedVault, CreditCard::class.java) as CreditCard?
                if (decrypted != null) {
                    decrypted.id = item.id
                    decrypted.createdDate = item.dateCreated
                    decrypted.modifiedDate = item.dateModified
                    cards.add(decrypted)
                }
            }
            decryptedCards.value = cards
        }

        return decryptedCards
    }

    fun getAllNotesLive(observer: LifecycleOwner): LiveData<ArrayList<Note>> {
        val encryptedNotes = userDataRepository.getLocalNotesLive()

        val decryptedNotes = MutableLiveData<ArrayList<Note>>()
        decryptedNotes.value = ArrayList()

        encryptedNotes.observe(observer) {
            val notes = ArrayList<Note>()
            it.forEach { item ->
                val modifiedNote = EncryptedData(
                    id=item.id,
                    encryptedData = item.encryptedData,
                    iv=item.iv,
                    dateCreated = item.dateCreated,
                    dateModified = item.dateModified
                )
                val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(modifiedNote, Note::class.java) as Note?
                if (decrypted != null) {
                    decrypted.id = item.id
                    decrypted.createDate = item.dateCreated
                    decrypted.modifiedDate = item.dateModified
                    notes.add(decrypted)
                }
            }
            decryptedNotes.value = notes
        }

        return decryptedNotes
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

    fun getVaultById(id: String?): Vault? {
        if(id == null) { return null }

        val encVault = userDataRepository.getLocalVaultById(id)
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
}