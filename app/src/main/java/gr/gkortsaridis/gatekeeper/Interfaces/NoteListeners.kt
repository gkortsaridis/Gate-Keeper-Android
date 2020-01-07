package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.Note
import java.lang.Exception

interface NoteCreateListener {
    fun onNoteCreated(note: Note)
}

interface NoteRetrieveListener {
    fun onNotesRetrieved(notes: ArrayList<Note>)
    fun onNotesRetrievedError(e: Exception)
}

interface NoteClickListener {
    fun onNoteClicked(note: Note)
}
