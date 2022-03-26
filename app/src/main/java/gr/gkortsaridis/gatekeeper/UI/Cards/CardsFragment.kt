package gr.gkortsaridis.gatekeeper.UI.Cards


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
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
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.Composables.*
import gr.gkortsaridis.gatekeeper.UI.Logins.CreateLoginActivity
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperDevelopMockData
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperDevelopMockData.mockCards
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme
import gr.gkortsaridis.gatekeeper.ViewModels.MainViewModel

@AndroidEntryPoint
class CardsFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentVault = viewModel.getLastActiveVault()
        val allCardsLive = viewModel.getAllCardsLive(this)

        return ComposeView(requireContext()).apply {
            setContent { cardsPage(
                currentVault = currentVault,
                cards = allCardsLive,
            ) }
        }
    }

    @Preview
    @Composable
    fun cardsPage(
        currentVault: Vault = GateKeeperDevelopMockData.mockVault,
        cards: LiveData<ArrayList<CreditCard>> = GateKeeperDevelopMockData.mockCardsLive,
    ) {
        val cardsLive = cards.observeAsState()
        val currentVaultCards = MainViewModel.filterCardsByVault(cards = cardsLive.value ?: ArrayList(), vault = currentVault)

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
            if(currentVaultCards.isNotEmpty()) {
                itemsList(mockCards) //TODO: Use LIVE Cards when available
            } else {
                noCards()
            }
        }

    }

    @Composable
    fun itemsList(
        cards: ArrayList<CreditCard>,
    ){
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 32.dp, end= 32.dp, top = 8.dp, bottom = 60.dp)
        ) {
            items(cards) { card -> cardItem(card = card)}
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun cardItem(
        card: CreditCard,
    ) {
        var cardFace by remember {
            mutableStateOf(CardFace.Front)
        }

        GateKeeperFlipCard(
            cardFace = cardFace,
            front = {
                GateKeeperCardFront(
                    card = card,
                    vault = viewModel.getVaultById(card.vaultId) ?: GateKeeperDevelopMockData.mockVault,
                    onCardFlip = { cardFace = cardFace.next }
                )
            },
            back = {
                GateKeeperCardBack(
                    card = card,
                    vault = viewModel.getVaultById(card.vaultId) ?: GateKeeperDevelopMockData.mockVault,
                    onCardFlip = { cardFace = cardFace.next }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.567f)
                .padding(vertical = 8.dp)
        )
    }

    @Composable
    fun noCards() {
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

    private fun createCard() {
        startActivity(Intent(activity, CardEditActivity::class.java))
    }

    fun onCreditCardEditButtonClicked(card: CreditCard, position: Int) {
        val intent = Intent(activity, CardEditActivity::class.java)
        intent.putExtra("card_id", card.id)
        startActivity(intent)
    }

    private fun changeVault(currentVault: Vault) {
        val intent = Intent(activity, SelectVaultActivity::class.java)
        intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_ACTIVE_VAULT)
        intent.putExtra("vault_id",currentVault.id)
        startActivityForResult(intent, GateKeeperConstants.CHANGE_ACTIVE_VAULT_REQUEST_CODE)
    }
}
