package gr.gkortsaridis.gatekeeper.Repositories

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.VaultCreateListener
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
                    createVaultForUser("Personal", user, object : VaultCreateListener {
                        override fun onVaultCreated() { listener.onVaultSetupComplete() }
                        override fun onVaultCreateError() { listener.onVaultSetupError() }
                    })
                }
            }

            override fun onVaultsRetrieveError(e: Exception) {
                createVaultForUser("Personal", user, object : VaultCreateListener {
                    override fun onVaultCreated() { listener.onVaultSetupComplete() }
                    override fun onVaultCreateError() { listener.onVaultSetupError() }
                })
            }
        })

    }

    fun createVaultForUser(vaultName: String, user: FirebaseUser, listener: VaultCreateListener) {
        val db = FirebaseFirestore.getInstance()

        db.collection("vaults")
            .add(hashMapOf( "account_id" to user.uid, "name" to vaultName ))
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

    fun decryptVault(encryptedString: String): Vault {

        val response = CompletableFuture<Vault>()
        ECSymmetric().decrypt(encryptedString, GateKeeperApplication.user.uid, object :
            ECResultListener {
            override fun onFailure(message: String, e: Exception) {
                response.complete(null)
            }

            override fun <T> onSuccess(result: T) {
                response.complete(Gson().fromJson(result.toString(), Vault::class.java))
            }
        })

        return response.get()

    }

    fun getVaultByID(id: String): Vault? {
        for (vault in GateKeeperApplication.vaults) {
            if (vault.id == id) {
                return vault
            }
        }

        return null
    }
}