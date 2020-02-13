package gr.gkortsaridis.gatekeeper.UI.Cards


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.shapeofview.shapes.ArcView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.littlemango.stacklayoutmanager.StackLayoutManager
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


class CardsFragment : Fragment(), CreditCardClickListener, MyDialogFragmentListeners {

    private lateinit var cardsRecyclerView: RecyclerView
    private lateinit var addCreditCard: FloatingActionButton
    private lateinit var cardsAdapter: CreditCardsRecyclerViewAdapter
    private lateinit var vaultView: LinearLayout
    private lateinit var vaultName: TextView
    private lateinit var addCardButton: Button
    private lateinit var noCardsMessage: LinearLayout
    private lateinit var bottomArc: RelativeLayout
    private lateinit var cardNickname: TextView
    private lateinit var currentVault: Vault
    private lateinit var adView: AdView

    private lateinit var filtered: ArrayList<CreditCard>
    private var activeCard : CreditCard? = null
    private var activeCardVault : Vault? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cards, container, false)
        cardsRecyclerView = view.findViewById(R.id.cards_recycler_view)
        addCreditCard = view.findViewById(R.id.add_credit_card)
        vaultView = view.findViewById(R.id.vault_view)
        vaultName = view.findViewById(R.id.vault_name)
        addCardButton = view.findViewById(R.id.add_card_btn)
        noCardsMessage = view.findViewById(R.id.no_items_view)
        bottomArc = view.findViewById(R.id.bottom_arc)
        cardNickname = view.findViewById(R.id.card_nickname_tv)
        adView = view.findViewById(R.id.adview)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val stackLayoutManager = StackLayoutManager(StackLayoutManager.ScrollOrientation.BOTTOM_TO_TOP)
        stackLayoutManager.setItemOffset(50)
        cardsAdapter = CreditCardsRecyclerViewAdapter(activity!!, GateKeeperApplication.cards, this)
        cardsRecyclerView.adapter = cardsAdapter
        cardsRecyclerView.layoutManager = stackLayoutManager
        cardsRecyclerView.setHasFixedSize(true)
        stackLayoutManager.setItemChangedListener (object: StackLayoutManager.ItemChangedListener{
            override fun onItemChanged(position: Int) {
                activeCard = filtered[position]
                activeCardVault = VaultRepository.getVaultByID(activeCard?.vaultId ?: "")
                updateUI()
            }
        })

        addCardButton.setOnClickListener { createCard() }
        addCreditCard.setOnClickListener { createCard() }
        vaultView.setOnClickListener { changeVault() }
        animateFabIn()
        animateArcIn()
        return view
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
    private fun updateUI() {
        vaultName.text = VaultRepository.getLastActiveVault().name

        if (activeCard == null && filtered.isNotEmpty()) {
            activeCard = filtered[0]
            activeCardVault = VaultRepository.getVaultByID(activeCard!!.vaultId)
        }

        if (!cardsRecyclerView.isComputingLayout) {
            cardsAdapter.updateCards(filtered)
        }

        if (filtered.isNotEmpty()) {
            filtered.forEachIndexed { index, creditCard ->
                if (creditCard.id == activeCard?.id) {
                    if (bottomArc.alpha == 0.0f) { animateArcIn() }
                    //bottomArc.visibility = View.VISIBLE
                    cardNickname.text = "${index + 1}/${filtered.size} ${activeCard?.cardName}"
                }
            }
        } else {
            if (bottomArc.alpha == 1.0f) { animateArcOut() }
            //bottomArc.visibility = View.GONE
            cardNickname.text = ""
        }


        noCardsMessage.visibility = if (filtered.size > 0) View.GONE else View.VISIBLE
        addCreditCard.visibility = if (filtered.size > 0) View.VISIBLE else View.GONE
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
        updateUI()
    }

    private fun updateCards() {
        currentVault = VaultRepository.getLastActiveVault()
        filtered = CreditCardRepository.filterCardsByVault(currentVault)
        if (!filtered.contains(activeCard) && filtered.isNotEmpty()) {
            activeCard = filtered[0]
            activeCardVault = VaultRepository.getVaultByID(activeCard?.vaultId ?: "")
        }
    }

    override fun onDismissed() {
        super.onDismissed()
        updateUI()
    }

    private fun animateFabIn() {
        Timeline.createParallel()
            .push(Tween.to(addCreditCard, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(addCreditCard, Translation.XY).target(0f,-122.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(addCreditCard))
    }

    private fun animateArcIn() {
        Timeline.createParallel()
            .push(Tween.to(bottomArc, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(bottomArc, Translation.XY).target(0f,-122.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(bottomArc))
    }

    private fun animateArcOut() {
        Timeline.createParallel()
            .push(Tween.to(bottomArc, Alpha.VIEW, 1.0f).target(0.0f))
            .push(Tween.to(bottomArc, Translation.XY).target(0f,122.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(bottomArc))
    }

}
