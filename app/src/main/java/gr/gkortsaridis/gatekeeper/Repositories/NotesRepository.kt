package gr.gkortsaridis.gatekeeper.Repositories

import com.google.firebase.firestore.FirebaseFirestore
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.NoteCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.NoteDeleteListener
import gr.gkortsaridis.gatekeeper.Interfaces.NoteRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.NoteUpdateListener

object NotesRepository {

    fun filterNotesByVault(vault: Vault): ArrayList<Note> {
        val notes = GateKeeperApplication.notes

        val vaultIds = arrayListOf<String>()
        GateKeeperApplication.vaults.forEach { vaultIds.add(it.id) }
        val parentedNotes = ArrayList(notes.filter { vaultIds.contains(it.vaultId) })

        if (vault.id == "-1") { return parentedNotes }
        val filtered = parentedNotes.filter { it.vaultId == vault.id }
        return ArrayList(filtered)
    }

    fun createNote(note: Note, listener: NoteCreateListener?) {
        val db = FirebaseFirestore.getInstance()

        val notehash = hashMapOf(
            "note" to SecurityRepository.encryptObjectWithUserCredentials(note),
            "account_id" to AuthRepository.getUserID()
        )

        db.collection("notes")
            .add(notehash)
            .addOnCompleteListener {
                note.id = it.result?.id ?: ""
                listener?.onNoteCreated(note)
            }
    }

    fun deleteNote(note: Note, listener: NoteDeleteListener?) {
        val db = FirebaseFirestore.getInstance()
        db.collection("notes")
            .document(note.id)
            .delete()
            .addOnCompleteListener {
                listener?.onNoteDeleted()
            }
    }

    fun updateNote(note: Note, listener: NoteUpdateListener) {
        val notehash = hashMapOf(
            "note" to SecurityRepository.encryptObjectWithUserCredentials(note),
            "account_id" to AuthRepository.getUserID()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("notes")
            .document(note.id)
            .set(notehash)
            .addOnCompleteListener {
                listener.onNoteUpdated(note)
            }
    }

    fun getNoteById(noteId: String): Note? {
        for (note in GateKeeperApplication.notes) {
            if (noteId == note.id) {
                return note
            }
        }

        return null
    }

    fun retrieveNotesByAccountID(accountID: String, retrieveListener: NoteRetrieveListener) {

        val db = FirebaseFirestore.getInstance()
        db.collection("notes")
            .whereEqualTo("account_id",accountID)
            .get().addOnSuccessListener { result ->
                val notesResult = ArrayList<Note>()

                for (document in result) {
                    val encryptedNote = (document["note"] ?: "")as String
                    val decryptedNote = SecurityRepository.decryptStringToObjectWithUserCredentials(encryptedNote, Note::class.java) as Note?
                    if (decryptedNote != null) {
                        decryptedNote.id = document.id
                        notesResult.add(decryptedNote)
                    }
                }

                retrieveListener.onNotesRetrieved(notesResult)
            }
            .addOnFailureListener { exception -> retrieveListener.onNotesRetrievedError(exception) }

    }

}