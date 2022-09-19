package gr.gkortsaridis.gatekeeper.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.type.DateTime
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
    var date_created: Long? = null,
    var date_modified: Long? = null,
    var vault_id: String
) : Serializable