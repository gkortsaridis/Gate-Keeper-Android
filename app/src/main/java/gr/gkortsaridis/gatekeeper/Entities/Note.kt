package gr.gkortsaridis.gatekeeper.Entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import java.io.Serializable
import java.util.concurrent.CompletableFuture

data class Note( var title: String,
                 var body: String,
                 var createDate: Timestamp,
                 var modifiedDate: Timestamp,
                 var id: String,
                 var accountId: String,
                 var isPinned: Boolean,
                 var color: NoteColor?) {

    constructor(firestoreSnapShot: QueryDocumentSnapshot)
                        //Initialize with dummy data
            : this("","", Timestamp.now(), Timestamp.now(),"","", false, NoteColor.White) {

        //Then put the actual data on the object
        id = firestoreSnapShot.id
        accountId = firestoreSnapShot["accountId"] as String
        title = firestoreSnapShot["title"] as String
        body = firestoreSnapShot["body"] as String
        createDate = firestoreSnapShot["createDate"] as Timestamp
        modifiedDate = firestoreSnapShot["modifiedDate"] as Timestamp
        isPinned = firestoreSnapShot["isPinned"] as Boolean
        val c = firestoreSnapShot["color"] as String
        if (c == "White") {
            color = NoteColor.White
        }else if (c == "Red") {
            color = NoteColor.Red
        }else if (c == "Yellow") {
            color = NoteColor.Yellow
        }else if (c == "Blue") {
            color = NoteColor.Blue
        }else if (c == "Cream") {
            color = NoteColor.Cream
        }else if (c == "Green") {
            color = NoteColor.Green
        }else if (c == "Orange") {
            color = NoteColor.Orange
        }else if (c == "Pink") {
            color = NoteColor.Pink
        }
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