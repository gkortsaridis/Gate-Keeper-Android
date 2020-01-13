package gr.gkortsaridis.gatekeeper.Entities

import com.google.firebase.Timestamp

data class Note( var title: String,
                 var body: String,
                 var createDate: Timestamp,
                 var modifiedDate: Timestamp,
                 var id: String,
                 var accountId: String,
                 var isPinned: Boolean,
                 var color: NoteColor?)