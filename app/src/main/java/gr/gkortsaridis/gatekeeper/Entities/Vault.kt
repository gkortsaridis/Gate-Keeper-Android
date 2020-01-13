package gr.gkortsaridis.gatekeeper.Entities

import com.google.firebase.firestore.QueryDocumentSnapshot
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository

data class Vault( var id: String,
                  var account_id : String,
                  var name : String ) {

    constructor(firestoreSnapShot: QueryDocumentSnapshot) : this("","","") {
        id = firestoreSnapShot.id
        account_id = firestoreSnapShot["account_id"] as String
        name = SecurityRepository.decryptStringToObjectWithUserCredentials(firestoreSnapShot["name"] as String, String::class.java) as String
    }

    override fun toString(): String {
        return "Vault: [name: $name, account_id: $account_id]"
    }
}