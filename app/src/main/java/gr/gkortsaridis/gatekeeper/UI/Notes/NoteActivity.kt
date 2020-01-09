package gr.gkortsaridis.gatekeeper.UI.Notes

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.NoteColor
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.NotesRepository
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import java.text.SimpleDateFormat

class NoteActivity : AppCompatActivity() {

    private lateinit var note : Note

    private lateinit var toolbar: Toolbar
    private lateinit var noteBody : EditText
    private lateinit var noteModified: TextView
    private lateinit var noteExtraBtn: ImageButton
    private lateinit var bottomSheetLayout: LinearLayout
    private lateinit var bottomSheetContainer: RelativeLayout
    private lateinit var noteBackgroung: RelativeLayout

    private lateinit var circleBlue: Button
    private lateinit var circleCream: Button
    private lateinit var circleGreen: Button
    private lateinit var circleOrange: Button
    private lateinit var circlePink: Button
    private lateinit var circleRed: Button
    private lateinit var circleWhite: Button
    private lateinit var circleYellow: Button

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

        noteExtraBtn = findViewById(R.id.note_extra)
        bottomSheetLayout = findViewById(R.id.bottom_sheet)
        val sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        noteExtraBtn.setOnClickListener { sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED }

        noteBackgroung = findViewById(R.id.note_background)
        bottomSheetContainer = findViewById(R.id.bottom_container)
        circleBlue = findViewById(R.id.circle_blue)
        circleCream = findViewById(R.id.circle_cream)
        circleGreen = findViewById(R.id.circle_green)
        circleOrange = findViewById(R.id.circle_orange)
        circlePink = findViewById(R.id.circle_pink)
        circleRed = findViewById(R.id.circle_red)
        circleWhite = findViewById(R.id.circle_white)
        circleYellow = findViewById(R.id.circle_yellow)

        circleBlue.setOnClickListener { changeColor(NoteColor.Blue) }
        circleCream.setOnClickListener { changeColor(NoteColor.Cream) }
        circleGreen.setOnClickListener { changeColor(NoteColor.Green) }
        circleOrange.setOnClickListener { changeColor(NoteColor.Orange) }
        circlePink.setOnClickListener { changeColor(NoteColor.Pink) }
        circleRed.setOnClickListener { changeColor(NoteColor.Red) }
        circleWhite.setOnClickListener { changeColor(NoteColor.White) }
        circleYellow.setOnClickListener { changeColor(NoteColor.Yellow) }

        noteBody = findViewById(R.id.note_body)
        noteModified = findViewById(R.id.note_modified)
        val formatter = SimpleDateFormat(GateKeeperConstants.dateOnlyFormat)
        val formattedDate = formatter.format(note.modifiedDate.toDate())
        noteModified.text = "Edited at $formattedDate"
        noteBody.setText(note.body)
    }

    private fun changeColor(color: NoteColor) {
        noteBackgroung.setBackgroundColor(Color.parseColor(color.value))
        bottomSheetLayout.setBackgroundColor(Color.parseColor(color.value))
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