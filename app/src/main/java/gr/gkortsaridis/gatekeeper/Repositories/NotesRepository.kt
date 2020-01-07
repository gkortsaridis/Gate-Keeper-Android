package gr.gkortsaridis.gatekeeper.Repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Interfaces.NoteCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.NoteRetrieveListener
import java.util.concurrent.CompletableFuture

object NotesRepository {

    fun createNote(note: Note, listener: NoteCreateListener) {
        val db = FirebaseFirestore.getInstance()

        db.collection("notes")
            .add(hashMapOf( "account_id" to AuthRepository.getUserID(), "note" to note.encrypt() ))
            .addOnCompleteListener {
                listener.onNoteCreated(note)
            }
    }

    fun retrieveNotesByAccountID(accountID: String, retrieveListener: NoteRetrieveListener) {

        val db = FirebaseFirestore.getInstance()
        db.collection("notes")
            .whereEqualTo("account_id",accountID)
            .get().addOnSuccessListener { result ->
                val notesResult = ArrayList<Note>()

                for (document in result) {
                    val encryptedNote = (document["note"] ?: "")as String
                    val decryptedNote = decryptNote(encryptedNote)
                    if (decryptedNote != null) {
                        notesResult.add(decryptedNote)
                    }
                }

                retrieveListener.onNotesRetrieved(notesResult)
            }
            .addOnFailureListener { exception -> retrieveListener.onNotesRetrievedError(exception) }

    }

    fun decryptNote(encryptedNote: String) : Note? {
        val response = CompletableFuture<Note>()
        val userId = AuthRepository.getUserID()

        if (userId != "") {
            ECSymmetric().decrypt(encryptedNote, userId, object :
                ECResultListener {
                override fun onFailure(message: String, e: Exception) {
                    response.complete(null)
                }

                override fun <T> onSuccess(result: T) {
                    response.complete(Gson().fromJson(result.toString(), Note::class.java))
                }
            })

            return response.get()
        }else {
            return null
        }

    }
}