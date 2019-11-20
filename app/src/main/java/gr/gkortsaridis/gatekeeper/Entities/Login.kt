package gr.gkortsaridis.gatekeeper.Entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot

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
    }

    override fun toString(): String {
        return "Login: [ Name: "+this.name+
                ", Username: "+this.username+
                ", password: "+this.password+
                ", url: "+this.url+
                ", notes: "+this.notes+" ]"
    }
}