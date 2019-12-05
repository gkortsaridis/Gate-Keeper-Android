package gr.gkortsaridis.gatekeeper.UI.Cards


import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener

import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.CreditCardsRecyclerViewAdapter

class CardsFragment(private var activity: Activity) : Fragment(), CreditCardClickListener {

    private lateinit var cardsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cards, container, false)

        cardsRecyclerView = view.findViewById(R.id.cards_recycler_view)

        val cards = ArrayList<CreditCard>()
        val creditCard = CreditCard(cardName = "My Main Card", type = CardType.Visa, number = "1234 5678 9012 3456", expirationDate = "11/22", cvv = "1234", cardholderName = "Georgios Kortsaridis", accountId = AuthRepository.getUserID())
        cards.add(creditCard)
        cards.add(creditCard)

        val cardsAdapter = CreditCardsRecyclerViewAdapter(activity, cards, this)
        cardsRecyclerView.adapter = cardsAdapter
        cardsRecyclerView.layoutManager = LinearLayoutManager(activity)


        return view
    }


}
