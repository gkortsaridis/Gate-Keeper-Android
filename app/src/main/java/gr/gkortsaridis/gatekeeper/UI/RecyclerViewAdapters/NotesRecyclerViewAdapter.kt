package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Interfaces.NoteClickListener
import gr.gkortsaridis.gatekeeper.R
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class NotesRecyclerViewAdapter(
    private val context: Context,
    private val notes: ArrayList<Note>,
    private val listener: NoteClickListener): RecyclerView.Adapter<NotesRecyclerViewAdapter.NoteStaggeredViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteStaggeredViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_note, parent, false)
        return NoteStaggeredViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: NoteStaggeredViewHolder, position: Int) {
        val noteItem = notes[position]
        holder.bindView(noteItem, listener)
    }
    class NoteStaggeredViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var noteTitle: TextView? = null
        private var noteBody: TextView? = null
        private var noteModified: TextView? = null
        private var view: LinearLayout? = null

        init {
            noteTitle = v.findViewById(R.id.note_title)
            noteBody = v.findViewById(R.id.note_body)
            noteModified = v.findViewById(R.id.note_modified)
            view = v.findViewById(R.id.note_container)
        }

        fun bindView(note: Note, listener: NoteClickListener) {
            noteTitle?.text = note.title
            noteBody?.text = note.body
            val modifiedDate = note.modifiedDate.toDate()
            val formatter = SimpleDateFormat("dd/mm/yy hh:mm")
            var formattedDate = formatter.format(modifiedDate)
            noteModified?.text = formattedDate
        }
    }
}