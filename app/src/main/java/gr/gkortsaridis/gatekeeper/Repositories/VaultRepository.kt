package gr.gkortsaridis.gatekeeper.Repositories

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.VaultCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultEditListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultSetupListener
import java.util.concurrent.CompletableFuture

object VaultRepository {

    fun setupVaultsForNewUser(user: FirebaseUser, listener: VaultSetupListener) {

        retrieveVaultsByAccountID(user.uid, object: VaultRetrieveListener {
            override fun onVaultsRetrieveSuccess(vaults: ArrayList<Vault>) {
                if (vaults.size > 0) {
                    listener.onVaultSetupComplete()
                }else{
                    createVault("Personal", object : VaultCreateListener {
                        override fun onVaultCreated() { listener.onVaultSetupComplete() }
                        override fun onVaultCreateError() { listener.onVaultSetupError() }
                    })
                }
            }

            override fun onVaultsRetrieveError(e: Exception) {
                createVault("Personal", object : VaultCreateListener {
                    override fun onVaultCreated() { listener.onVaultSetupComplete() }
                    override fun onVaultCreateError() { listener.onVaultSetupError() }
                })
            }
        })

    }

    fun createVault(vaultName: String, listener: VaultCreateListener) {
        val db = FirebaseFirestore.getInstance()

        db.collection("vaults")
            .add(hashMapOf( "account_id" to AuthRepository.getUserID(), "name" to SecurityRepository.encryptObjectWithUserCredentials(vaultName) ))
            .addOnCompleteListener {
                if (it.isSuccessful) { listener.onVaultCreated() }
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
                    vaultsResult.add(Vault(document))
                }

                retrieveListener.onVaultsRetrieveSuccess(vaultsResult)
            }
            .addOnFailureListener { exception -> retrieveListener.onVaultsRetrieveError(exception) }

    }

    fun getVaultByID(id: String): Vault? {
        for (vault in GateKeeperApplication.vaults) {
            if (vault.id == id) {
                return vault
            }
        }

        return null
    }

    fun setActiveVault(vault: Vault) { DataRepository.lastActiveVaultId = vault.id }

    fun getLastActiveVault(): Vault {
        val lastActiveVaultId = DataRepository.lastActiveVaultId ?: ""
        var vaultToReturn = GateKeeperApplication.vaults[0]
        if (lastActiveVaultId != "") {
            val savedVault = getVaultByID(lastActiveVaultId)
            if (savedVault != null) { vaultToReturn = savedVault}
        }

        return vaultToReturn
    }

    fun renameVault(newName: String, vault: Vault, listener: VaultEditListener) {

        vault.name = newName

        val vaulthash = hashMapOf(
            "name" to SecurityRepository.encryptObjectWithUserCredentials(vault.name),
            "account_id" to AuthRepository.getUserID()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("vaults")
            .document(vault.id)
            .set(vaulthash)
            .addOnCompleteListener {
                listener.onVaultRenamed()
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