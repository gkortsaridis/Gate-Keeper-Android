package gr.gkortsaridis.gatekeeper.UI.Notes

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.NotesRepository
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import java.text.SimpleDateFormat

class NoteActivity : AppCompatActivity() {

    private lateinit var note : Note

    private lateinit var toolbar: Toolbar
    private lateinit var noteBody : EditText
    private lateinit var noteModified: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val noteId = intent.getStringExtra("note_id")!!
        note = NotesRepository.getNoteById(noteId)!!

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = note.title

        noteBody = findViewById(R.id.note_body)
        noteModified = findViewById(R.id.note_modified)
        val formatter = SimpleDateFormat(GateKeeperConstants.simpleDateFormat)
        val formattedDate = formatter.format(note.modifiedDate.toDate())
        noteModified.text = "Modified at $formattedDate"
        noteBody.setText(note.body)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                //Save Note
                val intent = Intent()
                setResult(1, intent)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}