package gr.gkortsaridis.gatekeeper.UI.Notes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.NoteColor
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
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
import gr.gkortsaridis.gatekeeper.Utils.showKeyboard
import kotlinx.android.synthetic.main.activity_note.*
import java.sql.Timestamp
import java.text.SimpleDateFormat


class NoteActivity : AppCompatActivity() {


    private lateinit var noteColor: NoteColor
    private lateinit var note : Note
    private var noteMenu : Int? = null
    private var isPinned : Boolean = false

    private lateinit var vaultToAdd: Vault

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val noteId = intent.getStringExtra("note_id")!!
        if (noteId != "-1") {
            delete_note_btn.visibility = View.VISIBLE
            note = NotesRepository.getNoteById(noteId)!!
            vaultToAdd = VaultRepository.getVaultByID(note.vaultId)!!
            this.isPinned = note.isPinned
            noteMenu = if (note.isPinned) R.menu.note_actionbar_menu_star_on else R.menu.note_actionbar_menu_star_off
        }else {
            delete_note_btn.visibility = View.INVISIBLE
            vaultToAdd = VaultRepository.getLastActiveRealVault()
            note = Note(
                title= "",
                body = "",
                modifiedDate = Timestamp(System.currentTimeMillis()),
                createDate = Timestamp(System.currentTimeMillis()),
                id= "-1",
                accountId = AuthRepository.getUserID(),
                isPinned = false,
                vaultId = vaultToAdd.id,
                color = NoteColor.White)
            noteMenu = R.menu.note_actionbar_menu_star_off
            note_body_et.requestFocus()
            note_body_et.showKeyboard()
        }

        noteColor = note.color ?: NoteColor.White
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        note_title_et.setText(note.title)
        delete_note_btn.setOnClickListener { deleteNote() }
        vault_view.setOnClickListener { changeVault() }

        val formatter = SimpleDateFormat(GateKeeperConstants.dateOnlyFormat)
        val formattedDate = formatter.format(note.modifiedDate)
        date_modified_tv.text = "Edited at $formattedDate"
        note_body_et.setText(note.body)

        update_note_btn.setOnClickListener {
            updateNoteAndFinish()
        }

        note_title_et.addTextChangedListener { toggleSaveButton() }
        note_body_et.addTextChangedListener{ toggleSaveButton() }

        updateUI()
        toggleSaveButton()
    }

    private fun toggleSaveButton() {
        update_note_btn.visibility = if (isNoteNotEmpty()) View.VISIBLE else View.INVISIBLE
    }

    private fun updateUI() {
        vault_name.text = vaultToAdd.name
        vault_view.setBackgroundColor(resources.getColor(vaultToAdd.getVaultColorResource()))
        vault_name.setTextColor(resources.getColor(vaultToAdd.getVaultColorAccent()))
        vault_icon.setColorFilter(resources.getColor(vaultToAdd.getVaultColorAccent()))
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
            android.R.id.home -> { finish() }
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

    private fun deleteNote() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Note")
        builder.setMessage("Are you sure you want to delete this Note item?")
        builder.setPositiveButton("DELETE"){dialog, _ ->
            dialog.cancel()

            val viewDialog = ViewDialog(this)
            viewDialog.showDialog()

            NotesRepository.deleteNote(note, object: NoteDeleteListener{
                override fun onNoteDeleted() {
                    NotesRepository.removeLocalNote(note)
                    //GateKeeperApplication.notes.remove(note)
                    viewDialog.hideDialog()
                    finishWithResult()
                }

                override fun onNoteDeleteError(errorCode: Int, errorMsg: String) {
                    NotesRepository.removeLocalNote(note)
                    //GateKeeperApplication.notes.remove(note)
                    viewDialog.hideDialog()
                }
            })

        }
        builder.setNegativeButton("CANCEL"){dialog, _ -> dialog.cancel() }
        val dialog: AlertDialog = builder.create()
        dialog.show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(resources.getColor(R.color.error_red))
    }

    private fun updateNoteAndFinish() {
        val viewDialog = ViewDialog(this)

        if (VaultRepository.getLastActiveVault().id != vaultToAdd.id) {
            VaultRepository.setActiveVault(VaultRepository.allVaultsObj)
        }
        if (note.id != "-1") {
            if (isNoteChanged()) {
                bringNoteObjUpToDate()

                viewDialog.showDialog()
                NotesRepository.updateNote(note, object : NoteUpdateListener{
                    override fun onNoteUpdated(note: Note) {
                        NotesRepository.updateLocalNote(note)
                        //GateKeeperApplication.notes.replaceAll { if (it.id == note.id) note else it }
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
            if (note_title_et.text.toString().trim() != "" || note_body_et.text.toString().trim() != "") {
                note.createDate = null
                bringNoteObjUpToDate()

                viewDialog.showDialog()
                NotesRepository.createNote(note, object : NoteCreateListener{
                    override fun onNoteCreated(note: Note) {
                        NotesRepository.addLocalNote(note)
                        //GateKeeperApplication.notes.add(note)
                        viewDialog.hideDialog()
                        finishWithResult()
                    }

                    override fun onNoteCreateError(errorCode: Int, errorMsg: String) {
                        NotesRepository.addLocalNote(note)
                        //GateKeeperApplication.notes.add(note)
                        viewDialog.hideDialog()
                        finishWithResult()
                    }
                })
            } else {
                Toast.makeText(this, "Cannot save empty note", Toast.LENGTH_SHORT).show()
                finishWithResult()
            }
        }

    }

    private fun isNoteNotEmpty(): Boolean {
        return note_title_et.text.toString().isNotEmpty() || note_body_et.text.toString().isNotEmpty()
    }

    private fun isNoteChanged(): Boolean {
        return (note.title != note_title_et.text.toString()
                || note.body != note_body_et.text.toString()
                || note.color != noteColor
                || this.isPinned != note.isPinned
                || this.vaultToAdd.id != note.vaultId)
    }

    private fun bringNoteObjUpToDate() {
        note.title = note_title_et.text.toString()
        note.body = note_body_et.text.toString()
        note.modifiedDate = null
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
            VaultRepository.setActiveVault(VaultRepository.allVaultsObj)
        }

        val intent = Intent()
        setResult(1, intent)
        finish()
    }
}