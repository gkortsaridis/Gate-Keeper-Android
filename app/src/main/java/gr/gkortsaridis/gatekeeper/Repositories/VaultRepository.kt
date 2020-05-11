package gr.gkortsaridis.gatekeeper.Repositories

import android.annotation.SuppressLint
import gr.gkortsaridis.gatekeeper.Database.GatekeeperDatabase
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.VaultColor
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.VaultCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultEditListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultSetupListener
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
object VaultRepository {

    val db = GatekeeperDatabase.getInstance(GateKeeperApplication.instance.applicationContext)
    val allVaultsObj =  Vault("-1", AuthRepository.getUserID(), "All Vaults", VaultColor.White)

    var allVaults: ArrayList<Vault>
        get() {
            return ArrayList(db.dao().allVaultsSync)
            //return GateKeeperApplication.vaults ?: ArrayList()
        }
        set(vaults) {
            db.dao().truncateVaults()
            for (vault in vaults) { db.dao().insertVault(vault) }
            //GateKeeperApplication.vaults = vaults
        }

    fun addLocalVault(vault: Vault) {
        db.dao().insertVault(vault)
    }

    fun removeLocalVault(vault: Vault) {
        db.dao().deleteVault(vault)
    }

    fun updateLocalVault(vault: Vault) {
        db.dao().updateVault(vault)
    }

    fun setupVaultsForNewUser(userId: String, listener: VaultSetupListener) {
        val personalVault = Vault(id = "", name = "Personal", account_id = AuthRepository.getUserID(), color = VaultColor.Blue)
        GateKeeperAPI.api.createVault(SecurityRepository.createEncryptedDataRequestBody(personalVault))
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe (
                {
                    if (it.errorCode == -1) { listener.onVaultSetupComplete() }
                    else { listener.onVaultSetupError(it.errorCode, it.errorMsg) }
                },
                { listener.onVaultSetupError(it.hashCode(), it.localizedMessage ?: "") }
            )
    }

    fun createVault(vault: Vault, listener: VaultCreateListener) {

        GateKeeperAPI.api.createVault(SecurityRepository.createEncryptedDataRequestBody(vault))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    if (it.errorCode == -1) {
                        val encryptedData = it.data
                        val vault = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(encryptedData, Vault::class.java) as Vault?
                        if (vault != null) {
                            vault.id = it.data.id.toString()
                            listener.onVaultCreated(vault)
                        } else {
                            listener.onVaultCreateError(-1, "Decryption Error")
                        }
                    }
                    else {
                        listener.onVaultCreateError(it.errorCode, it.errorMsg)
                    }
                },
                { listener.onVaultCreateError(it.hashCode(), it.localizedMessage ?: "") }
            )
    }

    fun getVaultByID(id: String): Vault? {
        if (id == "-1") { return Vault("-1", AuthRepository.getUserID(), "All Vaults", VaultColor.White) }

        for (vault in allVaults) {
            if (vault.id == id) {
                return vault
            }
        }

        return null
    }

    fun setActiveVault(vault: Vault) { DataRepository.lastActiveVaultId = vault.id }

    fun getLastActiveRealVault() : Vault {
        val lastActive = getLastActiveVault()
        return if (lastActive.id == "-1") { allVaults[0] }
        else lastActive
    }

    fun getLastActiveVault(): Vault {
        val lastActiveVaultId = DataRepository.lastActiveVaultId ?: ""
        var vaultToReturn = allVaults[0]
        if (lastActiveVaultId != "") {
            val savedVault = getVaultByID(lastActiveVaultId)
            if (savedVault != null) { vaultToReturn = savedVault}
        }

        return vaultToReturn
    }

    fun keepActiveVaultOrChangeToAllVaults(vaultId: String) {
        if (getLastActiveVault().id != vaultId) {
            setActiveVault(allVaultsObj)
        }
     }

    fun editVault(newName: String, color: VaultColor, vault: Vault, listener: VaultEditListener) {

        vault.name = newName
        vault.color = color

        GateKeeperAPI.api.updateVault(SecurityRepository.createEncryptedDataRequestBody(vault, vault.id))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    if (it.errorCode == -1) {
                        val encryptedData = it.data
                        val vault = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(encryptedData, Vault::class.java) as Vault?
                        if (vault != null) {
                            listener.onVaultEdited(vault)
                        } else {
                            listener.onVaultEditError(-1, "Decryption Error")
                        }
                    }
                    else {
                        listener.onVaultEditError(it.errorCode, it.errorMsg)
                    }
                },
                {
                    listener.onVaultEditError(it.hashCode(), it.localizedMessage ?: "")
                }
            )
    }

    fun deleteVault(vault: Vault, listener: VaultEditListener) {

        //Delete the vault
        GateKeeperAPI.api.deleteVault(vaultId = vault.id, body = SecurityRepository.createUsernameHashRequestBody())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    if (it.errorCode == -1 && vault.id.toLong() == it.deletedItemID) {

                        //Delete all vault's logins
                        val vaultLogins = LoginsRepository.filterLoginsByVault(LoginsRepository.allLogins, vault)
                        for (login in vaultLogins) {
                            LoginsRepository.deleteLogin(login, null)
                        }

                        //Delete all vault's cards
                        val vaultCards = CreditCardRepository.filterCardsByVault(CreditCardRepository.allCards, vault)
                        for (card in vaultCards) {
                            CreditCardRepository.deleteCreditCard(card, null)
                        }

                        //Delete all vault's notes
                        val vaultNotes = NotesRepository.filterNotesByVault(NotesRepository.allNotes, vault)
                        for (note in vaultNotes) {
                            NotesRepository.deleteNote(note, null)
                        }

                        listener.onVaultDeleted()
                    }
                    else { listener.onVaultDeleteError(it.errorCode, it.errorMsg) }
                },
                { listener.onVaultDeleteError(it.hashCode(), it.localizedMessage ?: "") }
            )
    }

    fun shouldCreateVaults(): Boolean {
        return false
    }

}