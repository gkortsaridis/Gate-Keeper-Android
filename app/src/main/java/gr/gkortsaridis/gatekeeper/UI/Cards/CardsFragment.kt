package gr.gkortsaridis.gatekeeper.UI.Cards


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.azoft.carousellayoutmanager.CarouselLayoutManager
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener
import com.azoft.carousellayoutmanager.CenterScrollListener
import com.google.android.gms.ads.AdRequest
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.Interfaces.MyDialogFragmentListeners
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

class CardsFragment : Fragment(), CreditCardClickListener, MyDialogFragmentListeners {

    private lateinit var filtered: ArrayList<CreditCard>
    private var activeCard : CreditCard? = null
    private var activeCardVault : Vault? = null
    private lateinit var cardsAdapter : CreditCardsRecyclerViewAdapter
    private lateinit var currentVault: Vault

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adRequest = AdRequest.Builder().build()
        adview.loadAd(adRequest)

        cardsAdapter = CreditCardsRecyclerViewAdapter(activity!!, GateKeeperApplication.cards, this)
        cards_recycler_view.adapter = cardsAdapter
        cards_recycler_view.addOnScrollListener(CenterScrollListener())

        val layoutManager = CarouselLayoutManager(CarouselLayoutManager.VERTICAL)
        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
        layoutManager.addOnItemSelectionListener {
            if (filtered.size > 0) {
                activeCard = filtered[it]
                activeCardVault = VaultRepository.getVaultByID(activeCard?.vaultId ?: "")
            }
            updateUI(updateCards = false)
        }
        cards_recycler_view.layoutManager = layoutManager
        cards_recycler_view.setHasFixedSize(true)

        add_card_btn.setOnClickListener { createCard() }
        add_credit_card.setOnClickListener { createCard() }
        vault_view.setOnClickListener { changeVault() }
        animateFabIn()
        //animateArcIn()
    }

    private fun createCard() {
        val cardDialogFragment = CardInfoFragment(card = null, isCreate = true, listeners = this)
        cardDialogFragment.show(fragmentManager!!, null)
        fragmentManager!!.registerFragmentLifecycleCallbacks(object: FragmentManager.FragmentLifecycleCallbacks(){
            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                super.onFragmentDestroyed(fm, f)
                onResume()

                fragmentManager!!.unregisterFragmentLifecycleCallbacks(this)
            }
        }, false)

    }

    @SuppressLint("RestrictedApi")
    private fun updateUI(updateCards: Boolean) {
        val vault = VaultRepository.getLastActiveVault()
        vault_name.text = vault.name
        vault_view.setBackgroundColor(resources.getColor(vault.getVaultColorResource()))
        vault_name.setTextColor(resources.getColor(vault.getVaultColorAccent()))
        vault_icon.setColorFilter(resources.getColor(vault.getVaultColorAccent()))

        if (activeCard == null && filtered.isNotEmpty()) {
            activeCard = filtered[0]
            activeCardVault = VaultRepository.getVaultByID(activeCard!!.vaultId)
        }

        if (!cards_recycler_view.isComputingLayout && updateCards) {
            cardsAdapter.updateCards(filtered)
            cards_recycler_view.scrollToPosition(0)
        }

        if (filtered.isNotEmpty()) {
            filtered.forEachIndexed { index, creditCard ->
                if (creditCard.id == activeCard?.id) {
                    //if (bottom_arc.alpha == 0.0f) { animateArcIn() }
                    card_nickname_tv.text = "${activeCard?.cardName}"
                }
            }
        } else {
            card_nickname_tv.text = ""
        }


        no_items_view.visibility = if (filtered.isNotEmpty()) View.GONE else View.VISIBLE
        add_credit_card.visibility = if (filtered.isNotEmpty()) View.VISIBLE else View.GONE
        card_name_container.visibility = if (filtered.isNotEmpty()) View.VISIBLE else View.GONE
    }

    override fun onCreditCardClicked(card: CreditCard) {
        val cardDialogFragment = CardInfoFragment(card = card, isCreate = false, listeners = this)
        cardDialogFragment.show(fragmentManager!!, null)
        fragmentManager!!.registerFragmentLifecycleCallbacks(object: FragmentManager.FragmentLifecycleCallbacks(){
            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                super.onFragmentDestroyed(fm, f)
                onResume()

                fragmentManager!!.unregisterFragmentLifecycleCallbacks(this)
            }
        }, false)
    }

    override fun onCreditCardEditButtonClicked(card: CreditCard, position: Int) {  }

    private fun changeVault() {
        val intent = Intent(activity, SelectVaultActivity::class.java)
        intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_ACTIVE_VAULT)
        intent.putExtra("vault_id",currentVault.id)
        startActivityForResult(intent, GateKeeperConstants.CHANGE_ACTIVE_VAULT_REQUEST_CODE)
    }

    override fun onResume() {
        super.onResume()
        updateCards()
        updateUI(updateCards = true)
    }

    private fun updateCards() {
        currentVault = VaultRepository.getLastActiveVault()
        filtered = CreditCardRepository.filterCardsByVault(currentVault)
        filtered.sortBy { it.modifiedDate }
        filtered.reverse()
        if (!filtered.contains(activeCard) && filtered.isNotEmpty()) {
            activeCard = filtered[0]
            activeCardVault = VaultRepository.getVaultByID(activeCard?.vaultId ?: "")
        }
    }

    override fun onDismissed() {
        super.onDismissed()
        updateUI(updateCards = true)
    }

    private fun animateFabIn() {
        Timeline.createParallel()
            .push(Tween.to(add_credit_card, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(add_credit_card, Translation.XY).target(0f,-100.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(add_credit_card))
    }

    private fun animateArcIn() {
        Timeline.createParallel()
            .push(Tween.to(bottom_arc, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(bottom_arc, Translation.XY).target(0f,-130.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(bottom_arc))
    }
}
