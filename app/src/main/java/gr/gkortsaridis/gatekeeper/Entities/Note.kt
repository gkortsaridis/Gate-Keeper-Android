package gr.gkortsaridis.gatekeeper.Entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import java.util.concurrent.CompletableFuture

class Note {
    var title: String
    var body: String
    var createDate: Timestamp
    var modifiedDate: Timestamp
    var id: String
    var accountId: String

    constructor(id: String, title: String, body: String, account_id: String, createDate: Timestamp, modifiedDate: Timestamp) {
        this.id = id
        this.title = title
        this.body = body
        this.createDate = createDate
        this.modifiedDate = modifiedDate
        this.accountId = account_id
    }


    constructor(firestoreSnapShot: QueryDocumentSnapshot) {
        id = firestoreSnapShot.id
        accountId = firestoreSnapShot["accountId"] as String
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