package gr.gkortsaridis.gatekeeper.Entities

import com.google.firebase.firestore.QueryDocumentSnapshot

class Folder {

    var id: String
    var account_id : String
    var name : String
    var parent_id : String

    constructor(firestoreSnapShot: QueryDocumentSnapshot) {
        id = firestoreSnapShot.id
        account_id = firestoreSnapShot["account_id"] as String
        name = firestoreSnapShot["name"] as String
        parent_id = firestoreSnapShot["parent_id"] as String
    }

    override fun toString(): String {
        return "Folder: [name: $name, parent_id$parent_id, account_id$account_id]"
    }
}