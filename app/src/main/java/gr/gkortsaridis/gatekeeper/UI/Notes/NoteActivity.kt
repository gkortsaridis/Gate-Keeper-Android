package gr.gkortsaridis.gatekeeper.UI.Notes

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
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.NoteColor
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.NoteCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.NoteDeleteListener
import gr.gkortsaridis.gatekeeper.Interfaces.NoteUpdateListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.NotesRepository
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import java.text.SimpleDateFormat


class NoteActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var noteTitle: EditText
    private lateinit var noteBody : EditText
    private lateinit var noteModified: TextView
    private lateinit var noteExtraBtn: ImageButton
    private lateinit var bottomSheetLayout: LinearLayout
    private lateinit var bottomSheetContainer: RelativeLayout
    private lateinit var noteBackground: RelativeLayout
    private lateinit var deleteNote: LinearLayout
    private lateinit var circleBlue: Button
    private lateinit var circleCream: Button
    private lateinit var circleGreen: Button
    private lateinit var circleOrange: Button
    private lateinit var circlePink: Button
    private lateinit var circleRed: Button
    private lateinit var circleWhite: Button
    private lateinit var circleYellow: Button

    private lateinit var noteColor: NoteColor
    private lateinit var note : Note
    private lateinit var sheetBehavior : BottomSheetBehavior<LinearLayout>
    private var noteMenu : Int? = null
    private var isPinned : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        toolbar = findViewById(R.id.toolbar)
        deleteNote = findViewById(R.id.delete_note)
        noteTitle = findViewById(R.id.note_title)
        noteExtraBtn = findViewById(R.id.note_extra)
        bottomSheetLayout = findViewById(R.id.bottom_sheet)
        noteBackground = findViewById(R.id.note_background)
        bottomSheetContainer = findViewById(R.id.bottom_container)
        circleBlue = findViewById(R.id.circle_blue)
        circleCream = findViewById(R.id.circle_cream)
        circleGreen = findViewById(R.id.circle_green)
        circleOrange = findViewById(R.id.circle_orange)
        circlePink = findViewById(R.id.circle_pink)
        circleRed = findViewById(R.id.circle_red)
        circleWhite = findViewById(R.id.circle_white)
        circleYellow = findViewById(R.id.circle_yellow)
        noteBody = findViewById(R.id.note_body)
        noteModified = findViewById(R.id.note_modified)

        val noteId = intent.getStringExtra("note_id")!!
        if (noteId != "-1") {
            deleteNote.visibility = View.VISIBLE
            note = NotesRepository.getNoteById(noteId)!!
            this.isPinned = note.isPinned
            noteMenu = if (note.isPinned) R.menu.note_actionbar_menu_star_on else R.menu.note_actionbar_menu_star_off
        }else {
            deleteNote.visibility = View.GONE
            note = Note(
                title= "",
                body = "",
                modifiedDate = Timestamp.now(),
                createDate = Timestamp.now(),
                id= "",
                accountId = AuthRepository.getUserID(),
                isPinned = false,
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
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        noteExtraBtn.setOnClickListener { sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED }

        circleBlue.setOnClickListener { changeColor(NoteColor.Blue) }
        circleCream.setOnClickListener { changeColor(NoteColor.Cream) }
        circleGreen.setOnClickListener { changeColor(NoteColor.Green) }
        circleOrange.setOnClickListener { changeColor(NoteColor.Orange) }
        circlePink.setOnClickListener { changeColor(NoteColor.Pink) }
        circleRed.setOnClickListener { changeColor(NoteColor.Red) }
        circleWhite.setOnClickListener { changeColor(NoteColor.White) }
        circleYellow.setOnClickListener { changeColor(NoteColor.Yellow) }

        val formatter = SimpleDateFormat(GateKeeperConstants.dateOnlyFormat)
        val formattedDate = formatter.format(note.modifiedDate.toDate())
        noteModified.text = "Edited at $formattedDate"
        noteBody.setText(note.body)

        changeColor(noteColor)
    }

    private fun changeColor(color: NoteColor) {
        noteColor = color
        noteBackground.setBackgroundColor(Color.parseColor(color.value))
        bottomSheetLayout.setBackgroundColor(Color.parseColor(color.value))
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
        super.onBackPressed()
        updateNoteAndFinish()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action === MotionEvent.ACTION_DOWN) {
            if (sheetBehavior.state === BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()
                bottomSheetLayout.getGlobalVisibleRect(outRect)

                if (!outRect.contains(ev?.rawX.toInt(), ev.rawY.toInt())){
                    sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }

        return super.dispatchTouchEvent(ev)
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
        })
    }

    private fun updateNoteAndFinish() {
        val viewDialog = ViewDialog(this)

        if (note.id != "") {
            if (note.title != noteTitle.text.toString()
                || note.body != noteBody.text.toString()
                || note.color != noteColor
                || this.isPinned != note.isPinned
            ) {

                note.title = noteTitle.text.toString()
                note.body = noteBody.text.toString()
                note.modifiedDate = Timestamp.now()
                note.color = noteColor
                note.isPinned = this.isPinned
                viewDialog.showDialog()

                NotesRepository.updateNote(note, object : NoteUpdateListener{
                    override fun onNoteUpdated(note: Note) {
                        GateKeeperApplication.notes.replaceAll { if (it.id == note.id) note else it }
                        viewDialog.hideDialog()
                        finishWithResult()
                    }
                })
            }

        }else {
            if (noteTitle.text.toString().trim() != "" || noteBody.text.toString().trim() != "") {
                viewDialog.showDialog()

                NotesRepository.createNote(note, object : NoteCreateListener{
                    override fun onNoteCreated(note: Note) {
                        GateKeeperApplication.notes.add(note)
                        viewDialog.hideDialog()
                        finishWithResult()
                    }
                })
            }
        }

    }

    private fun finishWithResult() {
        val intent = Intent()
        setResult(1, intent)
        finish()
    }
}