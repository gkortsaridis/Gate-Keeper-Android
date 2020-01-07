package gr.gkortsaridis.gatekeeper.Entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import java.util.concurrent.CompletableFuture

class Note(firestoreSnapShot: QueryDocumentSnapshot) {

    var title: String
    var body: String
    var createDate: Timestamp
    var modifiedDate: Timestamp
    var id: String
    var account_id: String

    init {
        id = firestoreSnapShot.id
        account_id = firestoreSnapShot["account_id"] as String
        title = firestoreSnapShot["title"] as String
        body = firestoreSnapShot["body"] as String
        createDate = firestoreSnapShot["createDate"] as Timestamp
        modifiedDate = firestoreSnapShot["modifiedDate"] as Timestamp
    }

    fun encrypt() : String {
        val decrypted = Gson().toJson(this)
        val response = CompletableFuture<String>()
        ECSymmetric().encrypt(decrypted, AuthRepository.getUserID(), object :
            ECResultListener {
            override fun onFailure(message: String, e: Exception) {
                response.complete("-1")
            }

            override fun <T> onSuccess(result: T) {
                response.complete(result as String)
            }
        })

        return response.get()
    }
}