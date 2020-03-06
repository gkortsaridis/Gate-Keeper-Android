package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.Note
import java.lang.Exception

interface NoteCreateListener {
    fun onNoteCreated(note: Note)
    fun onNoteCreateError(errorCode: Int, errorMsg: String)
}

interface NoteRetrieveListener {
    fun onNotesRetrieved(notes: ArrayList<Note>)
    fun onNotesRetrievedError(e: Exception)
}

interface NoteClickListener {
    fun onNoteClicked(note: Note)
}

interface NoteUpdateListener {
    fun onNoteUpdated(note: Note)
    fun onNoteUpdateError(errorCode: Int, errorMsg: String)
}

interface NoteDeleteListener {
    fun onNoteDeleted()
    fun onNoteDeleteError(errorCode: Int, errorMsg: String)
}