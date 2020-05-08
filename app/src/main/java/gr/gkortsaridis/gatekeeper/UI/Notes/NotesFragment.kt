package gr.gkortsaridis.gatekeeper.UI.Notes


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.AdRequest
import gr.gkortsaridis.gatekeeper.Database.MainViewModel
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Interfaces.NoteClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.NotesRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.NotesRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import gr.gkortsaridis.gatekeeper.Utils.dp
import io.noties.tumbleweed.Timeline
import io.noties.tumbleweed.Tween
import io.noties.tumbleweed.android.ViewTweenManager
import io.noties.tumbleweed.android.types.Alpha
import io.noties.tumbleweed.android.types.Translation
import io.noties.tumbleweed.equations.Cubic
import kotlinx.android.synthetic.main.fragment_notes.*

class NotesFragment : Fragment(), NoteClickListener {

    private var currentVault: Vault = VaultRepository.getLastActiveRealVault()
    private var notesAdapter: NotesRecyclerViewAdapter? = null
    private var allNotes: ArrayList<Note> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: MainViewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java)
        viewModel.appNotes.observe(activity!!, Observer {
            this.allNotes = ArrayList(it)
            updateUI()
        })

        currentVault = VaultRepository.getLastActiveVault()
        val adRequest = AdRequest.Builder().build()
        adview.loadAd(adRequest)

        notes_recycler_view.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        notesAdapter = NotesRecyclerViewAdapter(activity!!, getOrderedNotes(currentVault), this)
        notes_recycler_view.adapter = notesAdapter

        add_note_btn.setOnClickListener { addNote() }
        add_note.setOnClickListener { addNote() }
        vault_view.setOnClickListener { changeVault() }

        animateItemsIn()

    }

    private fun changeVault() {
        val intent = Intent(activity, SelectVaultActivity::class.java)
        intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_ACTIVE_VAULT)
        intent.putExtra("vault_id",currentVault.id)
        startActivityForResult(intent, GateKeeperConstants.CHANGE_ACTIVE_VAULT_REQUEST_CODE)
    }

    private fun addNote() {
        val intent = Intent(activity, NoteActivity::class.java)
        intent.putExtra("note_id", "-1")
        startActivityForResult(intent,0)
    }

    private fun updateUI() {
        val notes = getOrderedNotes(currentVault)
        if (notesAdapter == null) {
            notesAdapter = NotesRecyclerViewAdapter(activity!!, notes, this)
        }
        notesAdapter?.setNotes(notes)

        vault_name.text = currentVault.name
        vault_view.setBackgroundColor(resources.getColor(currentVault.getVaultColorResource()))
        vault_name.setTextColor(resources.getColor(currentVault.getVaultColorAccent()))
        vault_icon.setColorFilter(resources.getColor(currentVault.getVaultColorAccent()))

        no_items_view.visibility = if (notes.size > 0) View.GONE else View.VISIBLE
        add_note.visibility = if (notes.size > 0) View.VISIBLE else View.GONE
    }

    private fun getOrderedNotes(vault: Vault): ArrayList<Note> {
        val filtered = NotesRepository.filterNotesByVault(this.allNotes, vault)
        val pinnedNotes = filtered.filter { it.isPinned }
        val nonPinnedNotes = filtered.filter { !it.isPinned }

        val pinnedSorted = ArrayList(pinnedNotes.sortedWith(compareBy { it.modifiedDate }).reversed())
        val nonPinnedSorted = ArrayList(nonPinnedNotes.sortedWith(compareBy { it.modifiedDate }).reversed())
        pinnedSorted.addAll(nonPinnedSorted)

        return pinnedSorted
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

    override fun onResume() {
        super.onResume()
        currentVault = VaultRepository.getLastActiveVault()
        updateUI()
    }

    private fun animateItemsIn() {
        Timeline.createParallel()
            .push(Tween.to(add_note, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(add_note, Translation.XY).target(0f,-72.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            //.push(Tween.to(adview_container, Alpha.VIEW, 1.0f).target(1.0f))
            //.push(Tween.to(adview_container, Translation.XY).target(0f,-90.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(add_note))
    }

}
