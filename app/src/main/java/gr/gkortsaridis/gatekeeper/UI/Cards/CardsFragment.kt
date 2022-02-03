package gr.gkortsaridis.gatekeeper.UI.Cards


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.google.accompanist.pager.*
import com.google.android.material.math.MathUtils.lerp
import dagger.hilt.android.AndroidEntryPoint
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.Composables.GateKeeperVaultSelector
import gr.gkortsaridis.gatekeeper.UI.Logins.CreateLoginActivity
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.CreditCardsRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperDevelopMockData
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
            //if(currentVaultCards.isNotEmpty()) {
                cardsContent()
            //} else {
            //    noCards()
            //}
        }

    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun cardsContent() {
        Column(Modifier.fillMaxSize()) {
            val pagerState = rememberPagerState()

            // Display 10 items
            HorizontalPager(
                count = 10,
                state = pagerState,
                // Add 32.dp horizontal padding to 'center' the pages
                contentPadding = PaddingValues(horizontal = 48.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) { page ->
                Card(
                    modifier = Modifier
                        .graphicsLayer {
                            // Calculate the absolute offset for the current page from the
                            // scroll position. We use the absolute value which allows us to mirror
                            // any effects for both directions
                            val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                            // We animate the scaleX + scaleY, between 85% and 100%
                            lerp(
                                0.85f,
                                1f,
                                1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleX = scale
                                scaleY = scale
                            }

                            // We animate the alpha, between 50% and 100%
                            alpha = lerp(
                                0.8f,
                                1f,
                                1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                        .fillMaxWidth()
                        .aspectRatio(1.586f)
                ) {
                    Text("Card ${page}")
                }
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
            )
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
