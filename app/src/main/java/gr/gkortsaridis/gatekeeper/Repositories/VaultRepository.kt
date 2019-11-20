package gr.gkortsaridis.gatekeeper.Repositories

import com.google.firebase.firestore.FirebaseFirestore
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Interfaces.VaultRetrieveListener

object VaultRepository {

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

}