package gr.gkortsaridis.gatekeeper.Repositories

import android.annotation.SuppressLint
import com.google.firebase.firestore.FirebaseFirestore
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.NoteCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.NoteDeleteListener
import gr.gkortsaridis.gatekeeper.Interfaces.NoteRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.NoteUpdateListener
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
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
        GateKeeperAPI.api.createNote(SecurityRepository.createEncryptedDataRequestBody(note))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    val decryptedNote = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(it.data, Note::class.java) as Note?
                    if (decryptedNote != null) {
                        decryptedNote.id = it.data.id.toString()
                        if (it.errorCode == -1) { listener?.onNoteCreated(decryptedNote) }
                        else { listener?.onNoteCreateError(it.errorCode, it.errorMsg) }
                    } else {
                        listener?.onNoteCreateError(-1, "Decryption Error")
                    }
                },
                {
                    listener?.onNoteCreateError(it.hashCode(), it.localizedMessage ?: "")
                }
            )
    }

    fun deleteNote(note: Note, listener: NoteDeleteListener?) {
        GateKeeperAPI.api.deleteNote(noteId = note.id, body = SecurityRepository.createUsernameHashRequestBody())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    if (it.errorCode == -1 && note.id.toLong() == it.deletedItemID) { listener?.onNoteDeleted() }
                    else { listener?.onNoteDeleteError(it.errorCode, it.errorMsg) }
                },
                { listener?.onNoteDeleteError(it.hashCode(), it.localizedMessage ?: "") }
            )
    }

    fun updateNote(note: Note, listener: NoteUpdateListener) {
        GateKeeperAPI.api.updateNote(SecurityRepository.createEncryptedDataRequestBody(note))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    val decryptedNote = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(it.data, Note::class.java) as Note?
                    if (decryptedNote != null) {
                        decryptedNote.id = it.data.id.toString()
                        if (it.errorCode == -1) { listener.onNoteUpdated(decryptedNote) }
                        else { listener.onNoteUpdateError(it.errorCode, it.errorMsg) }
                    } else {
                        listener.onNoteUpdateError(-1, "Decryption Error")
                    }
                },
                { listener.onNoteUpdateError(it.hashCode(), it.localizedMessage ?: "") }
            )
    }

    fun getNoteById(noteId: String): Note? {
        for (note in GateKeeperApplication.notes) {
            if (noteId == note.id) {
                return note
            }
        }

        return null
    }

}