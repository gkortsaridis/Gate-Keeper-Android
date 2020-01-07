package gr.gkortsaridis.gatekeeper.UI.Notes


import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.Timestamp
import gr.gkortsaridis.gatekeeper.Entities.Note

import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.NotesRepository

class NotesFragment(private var activity: Activity) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        //val note = Note(id = "0",title = "Test1", body = "body 1", account_id = AuthRepository.getUserID(), createDate = Timestamp.now(), modifiedDate = Timestamp.now())
        //NotesRepository.createNote(note, null)

        return view
    }


}
