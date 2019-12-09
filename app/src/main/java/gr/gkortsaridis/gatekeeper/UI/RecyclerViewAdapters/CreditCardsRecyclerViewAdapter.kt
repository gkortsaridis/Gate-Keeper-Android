package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Bank
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.R

class CreditCardsRecyclerViewAdapter(
    private val context: Context,
    private val cards: ArrayList<CreditCard>,
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
        holder.bindCard(vaultItem, listener)
    }

    class CreditCardViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private var bankLogo: ImageView? = null
        private var cardType: ImageView? = null
        private var cardNumber: TextView? = null
        private var cardName: TextView? = null
        private var cardholderName: TextView? = null
        private var cardExpiryDate: TextView? = null
        private var view: View = v

        init {
            bankLogo = view.findViewById(R.id.bank_logo)
            cardType = view.findViewById(R.id.card_type)
            cardName = view.findViewById(R.id.card_name)
            cardNumber = view.findViewById(R.id.card_number)
            cardExpiryDate = view.findViewById(R.id.expiration_date)
            cardholderName = view.findViewById(R.id.cardholder_name)
        }

        fun bindCard(card: CreditCard, listener: CreditCardClickListener){
            this.cardNumber?.text = card.number
            this.cardName?.text = card.cardName
            this.cardExpiryDate?.text = card.expirationDate
            this.cardholderName?.text = card.cardholderName

            when (card.type) {
                CardType.Visa -> { cardType?.setImageResource(R.drawable.visa) }
                CardType.Amex -> { cardType?.setImageResource(R.drawable.amex) }
                CardType.DinersClub -> { cardType?.setImageResource(R.drawable.discover) } //TODO: change
                CardType.DiscoverCard -> { cardType?.setImageResource(R.drawable.discover) }
                CardType.Mastercard -> { cardType?.setImageResource(R.drawable.mastercard) }
            }

            when (card.bank) {
                Bank.Monzo -> { bankLogo?.setImageResource(R.drawable.monzo) }
                Bank.Barclays -> { bankLogo?.setImageResource(R.drawable.barclays) }
                Bank.HSBC -> { bankLogo?.setImageResource(R.drawable.hsbc) }
                Bank.Lloyds -> { bankLogo?.setImageResource(R.drawable.lloyds) }
            }

            //view.setOnClickListener{ listener.onVaultClicked(vault) }
            //this.vaultName?.text = vault.name
        }

    }
}