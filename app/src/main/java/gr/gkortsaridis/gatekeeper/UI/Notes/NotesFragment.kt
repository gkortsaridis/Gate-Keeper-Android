package gr.gkortsaridis.gatekeeper.UI.Notes


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import dagger.hilt.android.AndroidEntryPoint
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.Composables.GateKeeperNote
import gr.gkortsaridis.gatekeeper.UI.Composables.StaggeredVerticalGrid
import gr.gkortsaridis.gatekeeper.UI.Composables.vaultSelector
import gr.gkortsaridis.gatekeeper.UI.Logins.CreateLoginActivity
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperDevelopMockData
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme
import gr.gkortsaridis.gatekeeper.ViewModels.MainViewModel

@AndroidEntryPoint
class NotesFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentVault = viewModel.getLastActiveVault()
        val allNotesLive = viewModel.getAllNotesLive(this)

        return ComposeView(requireContext()).apply {
            setContent { notesPage(
                currentVault = currentVault,
                notes = allNotesLive,
            ) }
        }
    }

    @Preview
    @Composable
    fun notesPage(
        currentVault: Vault = GateKeeperDevelopMockData.mockVault,
        notes: LiveData<ArrayList<Note>> = GateKeeperDevelopMockData.mockNotesLive,
    ) {
        val notesLive = notes.observeAsState()
        val currentVaultNotes = MainViewModel.filterNotesByVault(notes = notesLive.value ?: ArrayList(), vault = currentVault)

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(GateKeeperTheme.light_grey)
        ) {
            vaultSelector(
                currentVault = currentVault,
                onVaultClick = { changeVault(currentVault) }
            )
            if(currentVaultNotes.isNotEmpty()) {
                itemsList(GateKeeperDevelopMockData.mockNotes) //TODO: Use LIVE Notes when available
            } else {
                noNotes()
            }
        }

    }

    @Composable
    fun itemsList(
        notes: ArrayList<Note>,
    ){
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp

        val orderedNotes = getOrderedNotes(notes)
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                StaggeredVerticalGrid(
                    maxColumnWidth = screenWidth,
                ) {
                    orderedNotes.forEach { it ->
                        GateKeeperNote(
                            note = it,
                            onNoteClick = { clicked ->  onNoteClicked(clicked) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun noNotes() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.credit_cards_grey),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp, 40.dp)
                        .padding(4.dp)
                )
                Text(
                    text = stringResource(id = R.string.no_cards_title),
                    modifier = Modifier.padding(top=8.dp),
                    fontWeight = FontWeight.Bold,
                    color = GateKeeperTheme.tone_black
                )
                Text(
                    text = stringResource(id = R.string.no_cards_message),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top=8.dp),
                    color = GateKeeperTheme.tone_black
                )
            }

            FloatingActionButton(
                onClick = {
                    startActivity(Intent(requireActivity(), CreateLoginActivity::class.java))
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .size(56.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "",
                    modifier = Modifier.padding(all=12.dp)
                )
            }
        }

    }


    private fun changeVault(currentVault: Vault) {
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

    private fun getOrderedNotes(notes: ArrayList<Note>): ArrayList<Note> {
        val pinnedNotes = notes.filter { it.isPinned }
        val nonPinnedNotes = notes.filter { !it.isPinned }

        val pinnedSorted = ArrayList(pinnedNotes.sortedWith(compareBy { it.modifiedDate }).reversed())
        val nonPinnedSorted = ArrayList(nonPinnedNotes.sortedWith(compareBy { it.modifiedDate }).reversed())
        pinnedSorted.addAll(nonPinnedSorted)

        return pinnedSorted
    }

    private fun onNoteClicked(note: Note) {
        val intent = Intent(activity, NoteActivity::class.java)
        intent.putExtra("note_id", note.id)
        startActivityForResult(intent,0)
    }

}
