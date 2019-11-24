package gr.gkortsaridis.gatekeeper.Repositories

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import gr.gkortsaridis.gatekeeper.Entities.Folder
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.FolderCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.FolderRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.FolderSetupListener

object FolderRepository {

    fun setupFoldersForNewUser(user: FirebaseUser, listener: FolderSetupListener) {
        retrieveFoldersByAccountID(user.uid, object: FolderRetrieveListener{
            override fun onFoldersRetrieveSuccess(folders: ArrayList<Folder>) {
                if (folders.size > 0) { listener.onFolderSetupComplete() }
                else{
                    createFolderForUser("Root", GateKeeperApplication.user, object : FolderCreateListener{
                        override fun onFolderCreated() { listener.onFolderSetupComplete() }
                        override fun onFolderCreateError() { listener.onFolderSetupError() }
                    })
                }
            }
            override fun onFoldersRetrieveError(e: Exception) {
                createFolderForUser("Root", GateKeeperApplication.user, object : FolderCreateListener{
                    override fun onFolderCreated() { listener.onFolderSetupComplete() }
                    override fun onFolderCreateError() { listener.onFolderSetupError() }
                })
            }
        })
    }

    fun createFolderForUser(folderName: String, user: FirebaseUser, listener: FolderCreateListener) {
        val db = FirebaseFirestore.getInstance()
        db.collection("folders")
            .add(hashMapOf( "account_id" to user.uid, "name" to folderName, "parent_id" to null ))
            .addOnCompleteListener {
                if (it.isSuccessful) { listener.onFolderCreated() }
                else { listener.onFolderCreateError() }
            }
    }

    fun retrieveFoldersByAccountID(accountID: String, retrieveListener: FolderRetrieveListener) {

        val db = FirebaseFirestore.getInstance()
        db.collection("folders")
            .whereEqualTo("account_id",accountID)
            .get().addOnSuccessListener { result ->
                val foldersResult = ArrayList<Folder>()
                for (document in result) {
                    foldersResult.add(Folder(document))
                }

                retrieveListener.onFoldersRetrieveSuccess(foldersResult)
            }
            .addOnFailureListener { exception -> retrieveListener.onFoldersRetrieveError(exception) }

    }
}