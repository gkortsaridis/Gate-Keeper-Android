package gr.gkortsaridis.gatekeeper.UI.Cards


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.google.accompanist.pager.*
import com.google.android.material.math.MathUtils.lerp
import dagger.hilt.android.AndroidEntryPoint
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.UI.Composables.CardFace
import gr.gkortsaridis.gatekeeper.UI.Composables.GateKeeperFlipCard
import gr.gkortsaridis.gatekeeper.UI.Composables.GateKeeperVaultSelector
import gr.gkortsaridis.gatekeeper.UI.Logins.CreateLoginActivity
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.CreditCardsRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperDevelopMockData
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperDevelopMockData.mockCards
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme
import gr.gkortsaridis.gatekeeper.ViewModels.MainViewModel
import kotlin.math.absoluteValue

@AndroidEntryPoint
class CardsFragment : Fragment(), CreditCardClickListener {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentVault = viewModel.getLastActiveVault()
        val allLoginsLive = viewModel.getAllCardsLive(this)

        return ComposeView(requireContext()).apply {
            setContent { cardsPage(
                currentVault = currentVault,
                cards = allLoginsLive,
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
            GateKeeperVaultSelector.vaultSelector(currentVault = currentVault)
            if(currentVaultCards.isNotEmpty() || true) {
                itemsList(mockCards)
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
            front = { cardFront(
                card = card,
                onCardFlip = { cardFace = cardFace.next }
            ) },
            back = { cardBack(
                card = card,
                onCardFlip = { cardFace = cardFace.next }
            ) },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.567f)
                .padding(vertical = 8.dp)
        )
    }

    @Composable
    fun cardFront(
        card: CreditCard,
        onCardFlip: () -> Unit
    ) {
        val vault = viewModel.getVaultById(card.vaultId)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(16.dp)
                    .background(vault?.color?.toColor() ?: GateKeeperTheme.white),
            )
            Column(modifier = Modifier
                .fillMaxSize()
                .background(color = GateKeeperTheme.error_red)
                .clickable { onCardFlip() }
            ) {

            }
        }


    }

    @Composable
    fun cardBack(
        card: CreditCard,
        onCardFlip: () -> Unit
    ) {
        val vault = viewModel.getVaultById(card.vaultId)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(16.dp)
                    .background(vault?.color?.toColor() ?: GateKeeperTheme.white),
            )
            Column(modifier = Modifier
                .fillMaxSize()
                .background(color = GateKeeperTheme.done_green)
                .clickable { onCardFlip() }
            ) {

            }
        }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val allCardsLive = viewModel.getAllCardsLive(this)

       /* cardsAdapter = CreditCardsRecyclerViewAdapter(requireActivity(), allCardsLive.value ?: ArrayList(), this)
        cards_recycler_view.adapter = cardsAdapter
        cards_recycler_view.addOnScrollListener(CenterScrollListener())

        val layoutManager = CarouselLayoutManager(CarouselLayoutManager.VERTICAL)
        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
        cards_recycler_view.layoutManager = layoutManager
        cards_recycler_view.setHasFixedSize(true)

        add_card_btn.setOnClickListener { createCard() }
        add_credit_card.setOnClickListener { createCard() }
        vault_view.setOnClickListener { changeVault() } */
    }

    private fun createCard() {
        startActivity(Intent(activity, CardEditActivity::class.java))
    }

    private fun updateUI() {
        val vault = viewModel.getLastActiveVault()
        //vault_name.text = vault.name
        //vault_view.setBackgroundColor(resources.getColor(vault.getVaultColorResource()))
        //vault_name.setTextColor(resources.getColor(vault.getVaultColorAccent()))
        //vault_icon.setColorFilter(resources.getColor(vault.getVaultColorAccent()))

        /*
        if (cardsAdapter == null) {
            cardsAdapter = CreditCardsRecyclerViewAdapter(activity!!, filtered, this)
        }
        if (cards_recycler_view?.isComputingLayout == false) {
            cardsAdapter?.updateCards(filtered)
            cards_recycler_view.scrollToPosition(0)
        }*/

        //no_items_view.visibility =  View.VISIBLE
        //add_credit_card.visibility = View.GONE
    }

    override fun onCreditCardClicked(card: CreditCard) { }

    override fun onCreditCardEditButtonClicked(card: CreditCard, position: Int) {
        val intent = Intent(activity, CardEditActivity::class.java)
        intent.putExtra("card_id", card.id)
        startActivity(intent)
    }

    private fun changeVault() {
        val intent = Intent(activity, SelectVaultActivity::class.java)
        intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_ACTIVE_VAULT)
       // intent.putExtra("vault_id",currentVault.id)
        startActivityForResult(intent, GateKeeperConstants.CHANGE_ACTIVE_VAULT_REQUEST_CODE)
    }

    override fun onResume() {
        super.onResume()
        updateCards()
        updateUI()
    }

    private fun updateCards() {

    }

    /*
    private fun animateFabIn() {
        Timeline.createParallel()
            .push(Tween.to(add_credit_card, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(add_credit_card, Translation.XY).target(0f,-72.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(add_credit_card))
    }*/

}
