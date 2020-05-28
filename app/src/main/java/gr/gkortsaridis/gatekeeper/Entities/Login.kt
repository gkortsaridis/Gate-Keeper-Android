package gr.gkortsaridis.gatekeeper.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.sql.Timestamp

@Entity(tableName = "logins")
data class Login(
    @PrimaryKey
    var id : String = "-1",
    var account_id: String,
    var name: String,
    var username: String,
    var password: String,
    var url: String,
    var notes: String?,
    var date_created: Timestamp? = null,
    var date_modified: Timestamp? = null,
    var vault_id: String
) : Serializable