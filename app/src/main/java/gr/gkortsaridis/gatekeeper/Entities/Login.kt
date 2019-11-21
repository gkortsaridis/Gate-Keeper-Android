package gr.gkortsaridis.gatekeeper.Entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import java.util.*
import java.util.concurrent.CompletableFuture

class Login {

    var id : String
    var account_id: String
    var name: String
    var username: String
    var password: String
    var url: String
    var notes: String
    var date_created: Timestamp
    var date_modified: Timestamp
    var vault_id: String
    var folder_id: String

    constructor() {
        this.id = "-1"
        this.account_id = GateKeeperApplication.userAccount.id!!
        this.name = "Test_name"
        this.username = "Test_username"
        this.password = "Test_password"
        this.url = "https://www.google.com"
        this.notes = "Test notes"
        this.date_created = Timestamp(Date())
        this.date_modified = Timestamp(Date())
        this.vault_id = "test_vault_id"
        this.folder_id = "test_folder_id"
    }

    constructor(firestoreSnapShot: QueryDocumentSnapshot) {
        this.id = firestoreSnapShot.id
        this.account_id = firestoreSnapShot["account_id"] as String
        this.name = firestoreSnapShot["name"] as String
        this.username = firestoreSnapShot["username"] as String
        this.password = firestoreSnapShot["password"] as String
        this.url = firestoreSnapShot["url"] as String
        this.notes = firestoreSnapShot["notes"] as String
        this.date_created = firestoreSnapShot["date_added"] as Timestamp
        this.date_modified = firestoreSnapShot["date_modified"] as Timestamp
        this.vault_id = firestoreSnapShot["vault_id"] as String
        this.folder_id = firestoreSnapShot["folder_id"] as String
    }

    fun encrypt() : String {
        val decrypted = Gson().toJson(this)
        val response = CompletableFuture<String>()
        ECSymmetric().encrypt(decrypted, GateKeeperApplication.userAccount.id as String, object :
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

    override fun toString(): String {
        return "Login: [ Name: "+this.name+
                ", Username: "+this.username+
                ", password: "+this.password+
                ", url: "+this.url+
                ", notes: "+this.notes+" ]"
    }
}