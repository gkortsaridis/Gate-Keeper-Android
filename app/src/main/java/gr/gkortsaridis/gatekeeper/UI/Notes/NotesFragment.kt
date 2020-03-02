package gr.gkortsaridis.gatekeeper.UI.Notes


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.florent37.shapeofview.shapes.RoundRectView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
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

class NotesFragment : Fragment(), NoteClickListener {

    private lateinit var addNoteFab : FloatingActionButton
    private lateinit var notesRecyclerView : RecyclerView
    private lateinit var notesAdapter: NotesRecyclerViewAdapter
    private lateinit var vaultView: LinearLayout
    private lateinit var vaultName: TextView
    private lateinit var noNotesMessage: LinearLayout
    private lateinit var addNoteBtn: Button
    private lateinit var adContainer: RoundRectView
    private lateinit var adView: AdView

    private lateinit var currentVault: Vault

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notes, container, false)
        currentVault = VaultRepository.getLastActiveVault()

        addNoteFab = view.findViewById(R.id.add_note)
        notesRecyclerView = view.findViewById(R.id.notes_recycler_view)
        vaultView = view.findViewById(R.id.vault_view)
        vaultName = view.findViewById(R.id.vault_name)
        noNotesMessage = view.findViewById(R.id.no_items_view)
        addNoteBtn = view.findViewById(R.id.add_note_btn)
        adContainer = view.findViewById(R.id.adview_container)
        adView = view.findViewById(R.id.adview)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        notesRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        notesAdapter = NotesRecyclerViewAdapter(activity!!, getOrderedNotes(currentVault), this)
        notesRecyclerView.adapter = notesAdapter

        addNoteBtn.setOnClickListener { addNote() }
        addNoteFab.setOnClickListener { addNote() }
        vaultView.setOnClickListener { changeVault() }

        animateItemsIn()
        return view
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
        notesAdapter.setNotes(notes)
        vaultName.text = currentVault.name
        noNotesMessage.visibility = if (notes.size > 0) View.GONE else View.VISIBLE
        addNoteFab.visibility = if (notes.size > 0) View.VISIBLE else View.GONE
    }

    private fun getOrderedNotes(vault: Vault): ArrayList<Note> {
        val filtered = NotesRepository.filterNotesByVault(vault)
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
            .push(Tween.to(addNoteFab, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(addNoteFab, Translation.XY).target(0f,-162.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .push(Tween.to(adContainer, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(adContainer, Translation.XY).target(0f,-90.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(addNoteFab))
    }

}
