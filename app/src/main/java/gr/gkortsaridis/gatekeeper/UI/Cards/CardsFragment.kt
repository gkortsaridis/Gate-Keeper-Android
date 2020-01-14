package gr.gkortsaridis.gatekeeper.UI.Cards


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.CreditCardRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.CreditCardsRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants

class CardsFragment(private var activity: Activity) : Fragment(), CreditCardClickListener {

    private lateinit var cardsRecyclerView: RecyclerView
    private lateinit var addCreditCard: FloatingActionButton
    private lateinit var cardsAdapter: CreditCardsRecyclerViewAdapter
    private lateinit var vaultView: LinearLayout
    private lateinit var vaultName: TextView
    private lateinit var noCardsMessage: TextView

    private lateinit var currentVault: Vault

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
        noCardsMessage = view.findViewById(R.id.no_cards_message)

        cardsAdapter = CreditCardsRecyclerViewAdapter(activity, GateKeeperApplication.cards, this)
        cardsRecyclerView.adapter = cardsAdapter
        cardsRecyclerView.layoutManager = GridLayoutManager(activity,1)

        addCreditCard.setOnClickListener { startActivity(Intent(activity, CreateCreditCardActivity::class.java)) }
        vaultView.setOnClickListener { changeVault() }

        return view
    }

    private fun updateUI() {
        currentVault = VaultRepository.getLastActiveVault()
        val filtered = CreditCardRepository.filterCardsByVault(currentVault)
        vaultName.text = currentVault.name
        cardsAdapter.updateCards(filtered)
        noCardsMessage.visibility = if (filtered.size > 0) View.GONE else View.VISIBLE
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
        startActivityForResult(intent, LoginsRepository.createLoginRequestCode)
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

}
