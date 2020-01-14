package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Bank
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Utils.dp

class CreditCardsRecyclerViewAdapter(
    private val context: Context,
    private var cards: ArrayList<CreditCard>,
    private val listener: CreditCardClickListener): RecyclerView.Adapter<CreditCardsRecyclerViewAdapter.CreditCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_card, parent, false)
        return CreditCardViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
        val vaultItem = cards[position]
        holder.bindCard(vaultItem, position, listener)
    }

    fun updateCards(cards: ArrayList<CreditCard>) {
        this.cards = cards
        notifyDataSetChanged()
    }

    class CreditCardViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private var cardType: ImageView? = null
        private var cardNumber: TextView? = null
        private var cardholderName: TextView? = null
        private var cardExpiryDateMonth: TextView? = null
        private var cardExpiryDateYear: TextView? = null
        private var cardCVV: TextView? = null
        private var view: View = v
        private var cardContainer: LinearLayout? = null

        init {
            cardType = view.findViewById(R.id.card_type)
            cardNumber = view.findViewById(R.id.card_number)
            cardExpiryDateMonth = view.findViewById(R.id.expire_month)
            cardExpiryDateYear = view.findViewById(R.id.expire_year)
            cardholderName = view.findViewById(R.id.cardholder_name)
            cardContainer = view.findViewById(R.id.card_container)
            cardCVV = view.findViewById(R.id.cvv)
        }

        fun bindCard(card: CreditCard, position: Int, listener: CreditCardClickListener){
            if (position == 0) {
                view.setPadding(0,20.dp, 0,0)
            }

            this.cardNumber?.text = card.number
            this.cardExpiryDateMonth?.text = card.expirationDate
            this.cardExpiryDateYear?.text = card.expirationDate
            this.cardCVV?.text = "CVV: "+card.cvv
            this.cardholderName?.text = card.cardholderName

            when (card.type) {
                CardType.Visa -> { cardType?.setImageResource(R.drawable.visa) }
                CardType.Amex -> { cardType?.setImageResource(R.drawable.amex) }
                CardType.DinersClub -> { cardType?.setImageResource(R.drawable.discover) } //TODO: change
                CardType.DiscoverCard -> { cardType?.setImageResource(R.drawable.discover) }
                CardType.Mastercard -> { cardType?.setImageResource(R.drawable.mastercard) }
            }

            cardContainer?.setOnClickListener{ listener.onCreditCardClicked(card) }
        }

    }
}