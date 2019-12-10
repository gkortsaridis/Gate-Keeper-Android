package gr.gkortsaridis.gatekeeper.UI.Cards


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardRetrieveListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.CreditCardRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.CreditCardsRecyclerViewAdapter

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

        return view
    }

    override fun onCreditCardsReceived(cards: ArrayList<CreditCard>) {
        val cardsAdapter = CreditCardsRecyclerViewAdapter(activity, cards, this)
        cardsRecyclerView.adapter = cardsAdapter
        cardsRecyclerView.layoutManager = GridLayoutManager(activity,1)
    }

    override fun onCreditCardsReceiveError(e: Exception) { }


}
