package gr.gkortsaridis.gatekeeper.UI.Cards


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.azoft.carousellayoutmanager.CarouselLayoutManager
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener
import com.azoft.carousellayoutmanager.CenterScrollListener
import com.google.android.gms.ads.AdRequest
import gr.gkortsaridis.gatekeeper.ViewModels.MainViewModel
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.CreditCardRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.CreditCardsRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import gr.gkortsaridis.gatekeeper.Utils.dp
import io.noties.tumbleweed.Timeline
import io.noties.tumbleweed.Tween
import io.noties.tumbleweed.android.ViewTweenManager
import io.noties.tumbleweed.android.types.Alpha
import io.noties.tumbleweed.android.types.Translation
import io.noties.tumbleweed.equations.Cubic
import kotlinx.android.synthetic.main.fragment_cards.*
import kotlinx.android.synthetic.main.fragment_cards.adview
import kotlinx.android.synthetic.main.fragment_cards.no_items_view
import kotlinx.android.synthetic.main.fragment_cards.vault_name
import kotlinx.android.synthetic.main.fragment_cards.vault_view

class CardsFragment : Fragment(), CreditCardClickListener {

    private var allCards: ArrayList<CreditCard> = ArrayList()
    private var filtered: ArrayList<CreditCard> = ArrayList()
    private var cardsAdapter : CreditCardsRecyclerViewAdapter? = null
    private lateinit var currentVault: Vault

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: MainViewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java)
        /*viewModel.appCards.observe(activity!!, Observer {
            this.allCards = ArrayList(it)
            updateCards()
            updateUI()
        })*/

        val adRequest = AdRequest.Builder().build()
        adview.loadAd(adRequest)

        cardsAdapter = CreditCardsRecyclerViewAdapter(activity!!, CreditCardRepository.allCards, this)
        cards_recycler_view.adapter = cardsAdapter
        cards_recycler_view.addOnScrollListener(CenterScrollListener())

        val layoutManager = CarouselLayoutManager(CarouselLayoutManager.VERTICAL)
        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
        cards_recycler_view.layoutManager = layoutManager
        cards_recycler_view.setHasFixedSize(true)

        add_card_btn.setOnClickListener { createCard() }
        add_credit_card.setOnClickListener { createCard() }
        vault_view.setOnClickListener { changeVault() }
        animateFabIn()
    }

    private fun createCard() {
        startActivity(Intent(activity, CardEditActivity::class.java))
    }

    private fun updateUI() {
        val vault = VaultRepository.getLastActiveVault()
        vault_name.text = vault.name
        vault_view.setBackgroundColor(resources.getColor(vault.getVaultColorResource()))
        vault_name.setTextColor(resources.getColor(vault.getVaultColorAccent()))
        vault_icon.setColorFilter(resources.getColor(vault.getVaultColorAccent()))

        if (cardsAdapter == null) {
            cardsAdapter = CreditCardsRecyclerViewAdapter(activity!!, filtered, this)
        }
        if (cards_recycler_view?.isComputingLayout == false) {
            cardsAdapter?.updateCards(filtered)
            cards_recycler_view.scrollToPosition(0)
        }

        no_items_view.visibility = if (filtered.isNotEmpty()) View.GONE else View.VISIBLE
        add_credit_card.visibility = if (filtered.isNotEmpty()) View.VISIBLE else View.GONE
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
        intent.putExtra("vault_id",currentVault.id)
        startActivityForResult(intent, GateKeeperConstants.CHANGE_ACTIVE_VAULT_REQUEST_CODE)
    }

    override fun onResume() {
        super.onResume()
        updateCards()
        updateUI()
    }

    private fun updateCards() {
        currentVault = VaultRepository.getLastActiveVault()
        filtered = CreditCardRepository.filterCardsByVault(allCards, currentVault)
        filtered.sortBy { it.modifiedDate }
        filtered.reverse()
    }

    private fun animateFabIn() {
        Timeline.createParallel()
            .push(Tween.to(add_credit_card, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(add_credit_card, Translation.XY).target(0f,-72.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(add_credit_card))
    }

}
