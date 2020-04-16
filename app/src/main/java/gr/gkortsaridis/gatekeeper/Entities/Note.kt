package gr.gkortsaridis.gatekeeper.Entities

import java.sql.Timestamp

data class Note(var title: String,
                var body: String,
                var createDate: Timestamp? = null,
                var modifiedDate: Timestamp? = null,
                var id: String,
                var accountId: String,
                var isPinned: Boolean,
                var vaultId: String,
                var color: NoteColor?)