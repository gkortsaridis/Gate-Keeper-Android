package gr.gkortsaridis.gatekeeper.UI.Notes


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.NoteClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.NotesRecyclerViewAdapter
import java.io.Serializable

class NotesFragment(private var activity: Activity) : Fragment(), NoteClickListener {

    private lateinit var addNoteFab : FloatingActionButton
    private lateinit var notesRecyclerView : RecyclerView
    private lateinit var notesAdapter: NotesRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        addNoteFab = view.findViewById(R.id.add_note)
        notesRecyclerView = view.findViewById(R.id.notes_recycler_view)
        notesRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        notesAdapter = NotesRecyclerViewAdapter(activity, GateKeeperApplication.notes, this)
        notesRecyclerView.adapter = notesAdapter
        addNoteFab.setOnClickListener { addNote() }

        return view
    }

    private fun addNote() {
        val intent = Intent(activity, NoteActivity::class.java)
        intent.putExtra("note_id", "-1")
        startActivityForResult(intent,0)
    }

    private fun updateUI() {
        notesAdapter.setNotes(GateKeeperApplication.notes)
    }

    override fun onNoteClicked(note: Note) {
        val intent = Intent(activity, NoteActivity::class.java)
        intent.putExtra("note_id", note.id)
        startActivityForResult(intent,0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        updateUI()
    }

}
