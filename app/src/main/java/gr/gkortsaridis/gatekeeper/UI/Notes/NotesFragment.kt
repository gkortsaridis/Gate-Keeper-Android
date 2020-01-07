package gr.gkortsaridis.gatekeeper.UI.Notes


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.common.internal.ResourceUtils
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.dp
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.NoteClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.GridItemDecoration
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.NotesRecyclerViewAdapter

class NotesFragment(private var activity: Activity) : Fragment(), NoteClickListener {


    private lateinit var notesRecyclerView : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        notesRecyclerView = view.findViewById(R.id.notes_recycler_view)
        notesRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        //This will for default android divider
        notesRecyclerView.adapter = NotesRecyclerViewAdapter(activity, GateKeeperApplication.notes, this)

        //val note = Note(id = "0",title = "Test1", body = "body 1", account_id = AuthRepository.getUserID(), createDate = Timestamp.now(), modifiedDate = Timestamp.now())
        //NotesRepository.createNote(note, null)

        return view
    }

    override fun onNoteClicked(note: Note) {
    }

}
