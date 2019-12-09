package gr.gkortsaridis.gatekeeper.UI.Cards


import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Bank
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardRetrieveListener

import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.CreditCardRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.CreditCardsRecyclerViewAdapter
import java.lang.Exception

class CardsFragment(private var activity: Activity) : Fragment(), CreditCardClickListener, CreditCardRetrieveListener {

    private lateinit var cardsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cards, container, false)

        cardsRecyclerView = view.findViewById(R.id.cards_recycler_view)
        CreditCardRepository.retrieveCardsByAccountID(AuthRepository.getUserID(), this)

        /*CreditCardRepository.encryptAndStoreCard(activity, creditCard, object : CreditCardCreateListener {
            override fun onCreditCardCreated() {
                Toast.makeText(activity, "Card Saved", Toast.LENGTH_SHORT).show()
            }

            override fun onCreditCardCreateError() {
                Toast.makeText(activity, "Error :(", Toast.LENGTH_SHORT).show()
            }
        })*/
        return view
    }

    override fun onCreditCardsReceived(cards: ArrayList<CreditCard>) {
        val cardsAdapter = CreditCardsRecyclerViewAdapter(activity, cards, this)
        cardsRecyclerView.adapter = cardsAdapter
        cardsRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onCreditCardsReceiveError(e: Exception) { }


}
