package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.NoteColor
import gr.gkortsaridis.gatekeeper.Entities.VaultColor
import gr.gkortsaridis.gatekeeper.Interfaces.NoteClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import java.text.SimpleDateFormat

class NotesRecyclerViewAdapter(
    private val context: Context,
    private var notes: ArrayList<Note>,
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
        holder.bindView(noteItem, context, listener)
    }

    fun setNotes(notes: ArrayList<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    class NoteStaggeredViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var noteTitle: TextView? = null
        private var noteBody: TextView? = null
        private var noteModified: TextView? = null
        private var view: LinearLayout? = null
        private var pinnedNote: ImageView? = null

        init {
            noteTitle = v.findViewById(R.id.note_title)
            noteBody = v.findViewById(R.id.note_body)
            noteModified = v.findViewById(R.id.note_modified)
            view = v.findViewById(R.id.note_container)
            pinnedNote = v.findViewById(R.id.note_pinned)
        }

        fun bindView(note: Note, context: Context, listener: NoteClickListener) {
            noteTitle?.text = note.title
            noteTitle?.setTextColor(context.resources.getColor(R.color.mate_black))
            noteBody?.text = note.body
            val modifiedDate = note.modifiedDate.toDate()
            val formatter = SimpleDateFormat(GateKeeperConstants.simpleDateFormat)
            val formattedDate = formatter.format(modifiedDate)
            noteModified?.text = formattedDate

            val vault = VaultRepository.getVaultByID(note.vaultId)
            when (vault?.color) {
                VaultColor.Red -> {
                    view?.setBackgroundResource(R.drawable.vault_color_red)
                }
                VaultColor.Green -> {
                    view?.setBackgroundResource(R.drawable.vault_color_green)
                }
                VaultColor.Blue -> {
                    view?.setBackgroundResource(R.drawable.vault_color_blue)
                }
                VaultColor.Yellow -> {
                    view?.setBackgroundResource(R.drawable.vault_color_yellow)
                }
                VaultColor.White -> {
                    view?.setBackgroundResource(R.drawable.vault_color_white)
                }
            }

            //view?.setBackgroundColor(Color.parseColor(note.color?.value  ?: NoteColor.White.value))
            view?.setOnClickListener { listener.onNoteClicked(note) }
            pinnedNote?.visibility = if (note.isPinned) View.VISIBLE else View.GONE
        }
    }
}