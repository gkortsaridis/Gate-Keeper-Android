package gr.gkortsaridis.gatekeeper.Repositories

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.VaultColor
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.VaultCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultEditListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultSetupListener

object VaultRepository {

    fun setupVaultsForNewUser(user: FirebaseUser, listener: VaultSetupListener) {
        val personalVault = Vault(id = "", name = "Personal", account_id = AuthRepository.getUserID(), color = VaultColor.White)
        retrieveVaultsByAccountID(user.uid, object: VaultRetrieveListener {
            override fun onVaultsRetrieveSuccess(vaults: ArrayList<Vault>) {
                if (vaults.size > 0) {
                    listener.onVaultSetupComplete()
                }else{
                    createVault(personalVault, object : VaultCreateListener {
                        override fun onVaultCreated(vault: Vault) { listener.onVaultSetupComplete() }
                        override fun onVaultCreateError() { listener.onVaultSetupError() }
                    })
                }
            }

            override fun onVaultsRetrieveError(e: Exception) {
                createVault(personalVault, object : VaultCreateListener {
                    override fun onVaultCreated(vault: Vault) { listener.onVaultSetupComplete() }
                    override fun onVaultCreateError() { listener.onVaultSetupError() }
                })
            }
        })
    }

    fun createVault(vault: Vault, listener: VaultCreateListener) {
        val db = FirebaseFirestore.getInstance()

        db.collection("vaults")
            .add(hashMapOf( "account_id" to AuthRepository.getUserID(), "vault" to SecurityRepository.encryptObjectWithUserCredentials(vault) ))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    vault.id = it.result!!.id
                    listener.onVaultCreated(vault)
                }
                else { listener.onVaultCreateError() }
            }
    }

    fun retrieveVaultsByAccountID(accountID: String, retrieveListener: VaultRetrieveListener) {

        val db = FirebaseFirestore.getInstance()
        db.collection("vaults")
            .whereEqualTo("account_id",accountID)
            .get().addOnSuccessListener { result ->
                val vaultsResult = ArrayList<Vault>()
                for (document in result) {
                    val encryptedVault = (document["vault"] ?: "") as String
                    val decryptedVault = SecurityRepository.decryptStringToObjectWithUserCredentials(encryptedVault, Vault::class.java) as Vault?
                    if (decryptedVault != null) {
                        decryptedVault.id = document.id
                        vaultsResult.add(decryptedVault)
                    }
                }

                retrieveListener.onVaultsRetrieveSuccess(vaultsResult)
            }
            .addOnFailureListener { exception -> retrieveListener.onVaultsRetrieveError(exception) }

    }

    fun getVaultByID(id: String): Vault? {
        if (id == "-1") { return Vault("-1", AuthRepository.getUserID(), "All Vaults", VaultColor.White) }

        for (vault in GateKeeperApplication.vaults) {
            if (vault.id == id) {
                return vault
            }
        }

        return null
    }

    fun setActiveVault(vault: Vault) { DataRepository.lastActiveVaultId = vault.id }

    fun getLastActiveRealVault() : Vault {
        val lastActive = getLastActiveVault()
        return if (lastActive.id == "-1") { GateKeeperApplication.vaults[0] }
        else lastActive
    }

    fun getLastActiveVault(): Vault {
        val lastActiveVaultId = DataRepository.lastActiveVaultId ?: ""
        var vaultToReturn = GateKeeperApplication.vaults[0]
        if (lastActiveVaultId != "") {
            val savedVault = getVaultByID(lastActiveVaultId)
            if (savedVault != null) { vaultToReturn = savedVault}
        }

        return vaultToReturn
    }

    fun editVault(newName: String, color: VaultColor, vault: Vault, listener: VaultEditListener) {

        vault.name = newName
        vault.color = color

        val vaulthash = hashMapOf(
            "vault" to SecurityRepository.encryptObjectWithUserCredentials(vault),
            "account_id" to AuthRepository.getUserID()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("vaults")
            .document(vault.id)
            .set(vaulthash)
            .addOnCompleteListener {
                listener.onVaultEdited(vault)
            }

    }

    fun deleteVault(vault: Vault, listener: VaultEditListener) {
        val db = FirebaseFirestore.getInstance()
        db.collection("vaults")
            .document(vault.id)
            .delete()
            .addOnCompleteListener {
                retrieveVaultsByAccountID(vault.account_id, object: VaultRetrieveListener{
                    override fun onVaultsRetrieveSuccess(vaults: ArrayList<Vault>) {
                        GateKeeperApplication.vaults = vaults
                        listener.onVaultDeleted()
                    }

                    override fun onVaultsRetrieveError(e: Exception) {
                        listener.onVaultDeleted()
                    }
                })
            }

        val vaultLogins = LoginsRepository.filterLoginsByVault(GateKeeperApplication.logins, vault)
        for (login in vaultLogins) {
            LoginsRepository.deleteLogin(login, null)
        }
    }

}