package gr.gkortsaridis.gatekeeper.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.type.DateTime
import java.sql.Timestamp

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey
    var id: String,
    var title: String,
    var body: String,
    var createDate: Long? = null,
    var modifiedDate: Long? = null,
    var accountId: String,
    var isPinned: Boolean,
    var vaultId: String,
    var color: NoteColor?
)