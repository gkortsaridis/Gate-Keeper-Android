package gr.gkortsaridis.gatekeeper.Repositories

import com.google.firebase.firestore.FirebaseFirestore
import gr.gkortsaridis.gatekeeper.Entities.Folder
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Interfaces.FolderRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginRetrieveListener

object FolderRepository {

    fun retrieveFoldersByAccountID(accountID: String, retrieveListener: FolderRetrieveListener) {

        val db = FirebaseFirestore.getInstance()
        db.collection("folders")
            .whereEqualTo("account_id",accountID)
            .get().addOnSuccessListener { result ->
                val foldersResult = ArrayList<Folder>()
                for (document in result) {
                    //foldersResult.add(Login(document))
                }

                retrieveListener.onFoldersRetrieveSuccess(foldersResult)
            }
            .addOnFailureListener { exception -> retrieveListener.onFoldersRetrieveError(exception) }

    }
}