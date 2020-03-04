package gr.gkortsaridis.gatekeeper.UI.Notes

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.Timestamp
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.NoteCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.NoteDeleteListener
import gr.gkortsaridis.gatekeeper.Interfaces.NoteUpdateListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.NotesRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import java.text.SimpleDateFormat


class NoteActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var noteTitle: EditText
    private lateinit var noteBody : EditText
    private lateinit var noteModified: TextView
    private lateinit var noteBackground: RelativeLayout
    private lateinit var deleteNote: LinearLayout
    private lateinit var bottomView: LinearLayout

    private lateinit var noteColor: NoteColor
    private lateinit var note : Note
    private var noteMenu : Int? = null
    private var isPinned : Boolean = false
    private lateinit var vaultView: LinearLayout
    private lateinit var vaultName: TextView

    private lateinit var vaultToAdd: Vault

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        toolbar = findViewById(R.id.toolbar)
        deleteNote = findViewById(R.id.delete_note)
        noteTitle = findViewById(R.id.note_title)
        noteBackground = findViewById(R.id.note_background)
        noteBody = findViewById(R.id.note_body)
        noteModified = findViewById(R.id.note_modified)
        vaultView = findViewById(R.id.vault_view)
        vaultName = findViewById(R.id.vault_name)
        bottomView = findViewById(R.id.bottom_view)

        val noteId = intent.getStringExtra("note_id")!!
        if (noteId != "-1") {
            deleteNote.visibility = View.VISIBLE
            note = NotesRepository.getNoteById(noteId)!!
            vaultToAdd = VaultRepository.getVaultByID(note.vaultId)!!
            this.isPinned = note.isPinned
            noteMenu = if (note.isPinned) R.menu.note_actionbar_menu_star_on else R.menu.note_actionbar_menu_star_off
        }else {
            deleteNote.visibility = View.GONE
            vaultToAdd = VaultRepository.getLastActiveRealVault()
            note = Note(
                title= "",
                body = "",
                modifiedDate = Timestamp.now(),
                createDate = Timestamp.now(),
                id= "",
                accountId = AuthRepository.getUserID(),
                isPinned = false,
                vaultId = vaultToAdd.id,
                color = NoteColor.White)
            noteMenu = R.menu.note_actionbar_menu_star_off
        }

        noteColor = note.color ?: NoteColor.White
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        noteTitle.setText(note.title)
        deleteNote.setOnClickListener { deleteNote() }
        vaultView.setOnClickListener { changeVault() }

        val formatter = SimpleDateFormat(GateKeeperConstants.dateOnlyFormat)
        val formattedDate = formatter.format(note.modifiedDate.toDate())
        noteModified.text = "Edited at $formattedDate"
        noteBody.setText(note.body)

        updateUI()
    }

    private fun updateUI() {
        vaultName.text = vaultToAdd.name
        when (vaultToAdd.color) {
            VaultColor.Red -> {
                bottomView.setBackgroundResource(R.drawable.vault_color_red)
            }
            VaultColor.Green -> {
                bottomView.setBackgroundResource(R.drawable.vault_color_green)
            }
            VaultColor.Blue -> {
                bottomView.setBackgroundResource(R.drawable.vault_color_blue)
            }
            VaultColor.Yellow -> {
                bottomView.setBackgroundResource(R.drawable.vault_color_yellow)
            }
            VaultColor.White -> {
                bottomView.setBackgroundResource(R.drawable.vault_color_white)
            }
        }
    }

    private fun changeVault() {
        val intent = Intent(this, SelectVaultActivity::class.java)
        intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_VAULT)
        intent.putExtra("vault_id",vaultToAdd.id)
        startActivityForResult(intent, GateKeeperConstants.CHANGE_VAULT_REQUEST_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(noteMenu!!, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> { updateNoteAndFinish() }
            R.id.action_star_off -> {
                this.isPinned = true
                noteMenu = R.menu.note_actionbar_menu_star_on
                invalidateOptionsMenu()
            }
            R.id.action_star_on -> {
                this.isPinned = false
                noteMenu = R.menu.note_actionbar_menu_star_off
                invalidateOptionsMenu()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        updateNoteAndFinish()
    }

    private fun deleteNote() {
        val viewDialog = ViewDialog(this)
        viewDialog.showDialog()

        NotesRepository.deleteNote(note, object: NoteDeleteListener{
            override fun onNoteDeleted() {
                GateKeeperApplication.notes.remove(note)
                viewDialog.hideDialog()
                finishWithResult()
            }

            override fun onNoteDeleteError(errorCode: Int, errorMsg: String) {
                GateKeeperApplication.notes.remove(note)
                viewDialog.hideDialog()
            }
        })
    }

    private fun updateNoteAndFinish() {
        val viewDialog = ViewDialog(this)

        VaultRepository.setActiveVault(vaultToAdd)
        if (note.id != "") {
            if (isNoteChanged()) {
                bringNoteObjUpToDate()

                viewDialog.showDialog()
                NotesRepository.updateNote(note, object : NoteUpdateListener{
                    override fun onNoteUpdated(note: Note) {
                        GateKeeperApplication.notes.replaceAll { if (it.id == note.id) note else it }
                        viewDialog.hideDialog()
                        finishWithResult()
                    }

                    override fun onNoteUpdateError(errorCode: Int, errorMsg: String) {
                        viewDialog.hideDialog()
                        finishWithResult()
                    }
                })
            } else {
              finishWithResult()
            }

        }else {
            if (noteTitle.text.toString().trim() != "" || noteBody.text.toString().trim() != "") {
                note.createDate = Timestamp.now()
                bringNoteObjUpToDate()

                viewDialog.showDialog()
                NotesRepository.createNote(note, object : NoteCreateListener{
                    override fun onNoteCreated(note: Note) {
                        GateKeeperApplication.notes.add(note)
                        viewDialog.hideDialog()
                        finishWithResult()
                    }

                    override fun onNoteCreateError(errorCode: Int, errorMsg: String) {
                        GateKeeperApplication.notes.add(note)
                        viewDialog.hideDialog()
                        finishWithResult()
                    }
                })
            } else {
              finishWithResult()
            }
        }

    }

    private fun isNoteChanged(): Boolean {
        return (note.title != noteTitle.text.toString()
                || note.body != noteBody.text.toString()
                || note.color != noteColor
                || this.isPinned != note.isPinned
                || this.vaultToAdd.id != note.vaultId)
    }

    private fun bringNoteObjUpToDate() {
        note.title = noteTitle.text.toString()
        note.body = noteBody.text.toString()
        note.modifiedDate = Timestamp.now()
        note.color = noteColor
        note.isPinned = this.isPinned
        note.vaultId = vaultToAdd.id
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GateKeeperConstants.CHANGE_VAULT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val vaultId = data!!.data.toString()
            vaultToAdd = VaultRepository.getVaultByID(vaultId)!!
            updateUI()
        }

    }

    private fun finishWithResult() {
        if (note.id != VaultRepository.getLastActiveVault().id) {
            VaultRepository.setActiveVault(VaultRepository.allVaults)
        }

        val intent = Intent()
        setResult(1, intent)
        finish()
    }
}