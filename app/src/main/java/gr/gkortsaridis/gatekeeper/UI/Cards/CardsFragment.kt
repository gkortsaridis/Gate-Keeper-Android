package gr.gkortsaridis.gatekeeper.UI.Cards


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.azoft.carousellayoutmanager.CarouselLayoutManager
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener
import com.azoft.carousellayoutmanager.CenterScrollListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.CreditCardRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.CreditCardsRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import gr.gkortsaridis.gatekeeper.Utils.LinePagerIndicatorDecoration

class CardsFragment(private var activity: Activity) : Fragment(), CreditCardClickListener {

    private lateinit var cardsRecyclerView: RecyclerView
    private lateinit var addCreditCard: FloatingActionButton
    private lateinit var cardsAdapter: CreditCardsRecyclerViewAdapter
    private lateinit var vaultView: LinearLayout
    private lateinit var vaultName: TextView
    private lateinit var addCardButton: Button
    private lateinit var noCardsMessage: LinearLayout
    private lateinit var cardholderNameET: EditText
    private lateinit var cardNumberET: EditText
    private lateinit var expirationDateET: EditText
    private lateinit var vaultET: EditText
    private lateinit var cvvET: EditText

    private lateinit var currentVault: Vault

    private lateinit var filtered: ArrayList<CreditCard>
    private var activeCard : CreditCard? = null

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
        cardholderNameET = view.findViewById(R.id.cardholder_name_et)
        cardNumberET = view.findViewById(R.id.card_number_et)
        expirationDateET = view.findViewById(R.id.expiration_date_et)
        vaultET = view.findViewById(R.id.vault_et)
        cvvET = view.findViewById(R.id.cvv_et)

        val carouselLayoutManager = CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL)
        carouselLayoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())

        cardsAdapter = CreditCardsRecyclerViewAdapter(activity, GateKeeperApplication.cards, this)
        cardsRecyclerView.adapter = cardsAdapter
        cardsRecyclerView.layoutManager = carouselLayoutManager
        cardsRecyclerView.setHasFixedSize(true)
        cardsRecyclerView.addOnScrollListener(object: CenterScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == 0) {
                    val item = carouselLayoutManager.centerItemPosition
                    activeCard = filtered[item]
                    updateUI()
                }
            }
        })
        cardsRecyclerView.addItemDecoration(LinePagerIndicatorDecoration())

        addCardButton.setOnClickListener { startActivity(Intent(activity, CreateCreditCardActivity::class.java)) }
        addCreditCard.setOnClickListener { startActivity(Intent(activity, CreateCreditCardActivity::class.java)) }
        vaultView.setOnClickListener { changeVault() }

        return view
    }

    private fun updateUI() {
        currentVault = VaultRepository.getLastActiveVault()
        filtered = CreditCardRepository.filterCardsByVault(currentVault)
        vaultName.text = currentVault.name
        cardsAdapter.updateCards(filtered)

        if (activeCard == null) { activeCard = filtered[0] }

        cardholderNameET.setText(activeCard?.cardholderName)
        cardNumberET.setText(activeCard?.number)
        expirationDateET.setText(activeCard?.expirationDate)
        cvvET.setText(activeCard?.cvv)
        vaultET.setText(VaultRepository.getVaultByID(activeCard?.vaultId ?: "")?.name ?: "")

        noCardsMessage.visibility = if (filtered.size > 0) View.GONE else View.VISIBLE
        addCreditCard.visibility = if (filtered.size > 0) View.VISIBLE else View.GONE
    }

    override fun onCreditCardClicked(card: CreditCard) {
        val intent = Intent(activity, CreateCreditCardActivity::class.java)
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
        updateUI()
    }

}
