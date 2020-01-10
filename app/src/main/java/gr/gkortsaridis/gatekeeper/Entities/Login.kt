package gr.gkortsaridis.gatekeeper.Entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import java.io.Serializable
import java.util.*
import java.util.concurrent.CompletableFuture

data class Login( var id : String = "temp_id",
                  var account_id: String,
                  var name: String,
                  var username: String,
                  var password: String,
                  var url: String,
                  var notes: String?,
                  var date_created: Timestamp,
                  var date_modified: Timestamp,
                  var vault_id: String): Serializable {

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

    override fun toString(): String {
        return "Login: [ Name: "+this.name+
                ", Username: "+this.username+
                ", password: "+this.password+
                ", url: "+this.url+
                ", notes: "+this.notes+" ]"
    }
}