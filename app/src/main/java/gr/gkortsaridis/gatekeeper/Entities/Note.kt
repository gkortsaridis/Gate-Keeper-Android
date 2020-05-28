package gr.gkortsaridis.gatekeeper.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey
    var id: String,
    var title: String,
    var body: String,
    var createDate: Timestamp? = null,
    var modifiedDate: Timestamp? = null,
    var accountId: String,
    var isPinned: Boolean,
    var vaultId: String,
    var color: NoteColor?
)