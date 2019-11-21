package gr.gkortsaridis.gatekeeper.Entities

import com.google.firebase.firestore.QueryDocumentSnapshot

class Vault {

    var id: String
    var account_id : String
    var name : String

    constructor(firestoreSnapShot: QueryDocumentSnapshot) {
        id = firestoreSnapShot.id
        account_id = firestoreSnapShot["account_id"] as String
        name = firestoreSnapShot["name"] as String
    }

    override fun toString(): String {
        return "Vault: [name: $name, account_id: $account_id]"
    }
}