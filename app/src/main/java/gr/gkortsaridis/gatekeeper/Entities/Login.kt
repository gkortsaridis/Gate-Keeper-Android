package gr.gkortsaridis.gatekeeper.Entities

import com.google.firebase.Timestamp
import java.io.Serializable

data class Login( var id : String = "temp_id",
                  var account_id: String,
                  var name: String,
                  var username: String,
                  var password: String,
                  var url: String,
                  var notes: String?,
                  var date_created: Timestamp,
                  var date_modified: Timestamp,
                  var vault_id: String): Serializable