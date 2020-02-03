package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wajahatkarim3.easyflipview.EasyFlipView
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants.CARD_STATE_DONE
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants.CARD_STATE_EDITED

class CreditCardsRecyclerViewAdapter(
    private val context: Context,
    private var cards: ArrayList<CreditCard>,
    private val listener: CreditCardClickListener): RecyclerView.Adapter<CreditCardsRecyclerViewAdapter.CreditCardViewHolder>() {

    private var states: ArrayList<Int> = ArrayList()

    init {
        for (card in cards) { states.add(CARD_STATE_DONE) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_card, parent, false)
        return CreditCardViewHolder(inflatedView, context)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
        val vaultItem = cards[position]
        holder.bindCard(vaultItem, states[position], position, listener)
    }

    fun updateCards(cards: ArrayList<CreditCard>, states: ArrayList<Int>) {
        this.cards = cards
        this.states = states
        notifyDataSetChanged()
    }

    class CreditCardViewHolder(v: View, context: Context): RecyclerView.ViewHolder(v) {

        private var cardType: ImageView? = null
        private var cardNumber: TextView? = null
        private var cardholderName: TextView? = null
        private var cardExpiryDate: TextView? = null
        private var view: View = v
        private var cardContainer: LinearLayout? = null
        private var context: Context = context
        private var flipCard: LinearLayout? = null
        private var flipBackCard: LinearLayout? = null
        private var flipView: EasyFlipView? = null

        init {
            cardType = view.findViewById(R.id.card_type)
            cardNumber = view.findViewById(R.id.card_number)
            cardExpiryDate = view.findViewById(R.id.expiry_date)
            cardholderName = view.findViewById(R.id.cardholder_name)
            cardContainer = view.findViewById(R.id.card_container)
            flipCard = view.findViewById(R.id.flip_card_btn)
            flipBackCard = view.findViewById(R.id.flip_card_back_btn)
            flipView = view.findViewById(R.id.flip_view)
        }

        fun bindCard(card: CreditCard, state:Int, position: Int, listener: CreditCardClickListener){

            if (state == CARD_STATE_EDITED) {
                cardContainer?.setBackgroundColor(context.resources.getColor(R.color.editable_card))
                flipCard?.visibility = View.GONE
            }else {
                cardContainer?.setBackgroundColor(context.resources.getColor(android.R.color.white))
                flipCard?.visibility = View.VISIBLE
            }

            this.cardNumber?.text = card.number
            this.cardExpiryDate?.text = card.expirationDate
            this.cardholderName?.text = card.cardholderName

            when (card.type) {
                CardType.Visa -> { cardType?.setImageResource(R.drawable.visa) }
                CardType.Amex -> { cardType?.setImageResource(R.drawable.amex) }
                CardType.DinersClub -> { cardType?.setImageResource(R.drawable.discover) } //TODO: change
                CardType.DiscoverCard -> { cardType?.setImageResource(R.drawable.discover) }
                CardType.Mastercard -> { cardType?.setImageResource(R.drawable.mastercard) }
            }

            flipCard?.setOnClickListener { flipView?.flipTheView() }
            flipBackCard?.setOnClickListener { flipView?.flipTheView() }
            cardContainer?.setOnClickListener{ listener.onCreditCardClicked(card) }
        }

    }
}