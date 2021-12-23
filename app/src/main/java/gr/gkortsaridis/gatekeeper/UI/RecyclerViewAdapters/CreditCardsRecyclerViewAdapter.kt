package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.wajahatkarim3.easyflipview.EasyFlipView
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.CreditCardRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.Utils.dp
import gr.gkortsaridis.gatekeeper.Utils.px


class CreditCardsRecyclerViewAdapter(
    private val context: Context,
    private var cards: ArrayList<CreditCard>,
    private val listener: CreditCardClickListener): RecyclerView.Adapter<CreditCardsRecyclerViewAdapter.CreditCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_card, parent, false)
        return CreditCardViewHolder(inflatedView, context)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
        val vaultItem = cards[position]
        holder.bindCard(vaultItem,  position, listener)
    }

    fun updateCards(cards: ArrayList<CreditCard>) {
        this.cards = cards
        notifyDataSetChanged()
    }

    class CreditCardViewHolder(v: View, context: Context): RecyclerView.ViewHolder(v) {

        private var cardType: ImageView? = null
        private var cardNumber: TextView? = null
        private var cardholderName: TextView? = null
        private var cardExpiryDate: TextView? = null
        private var view: View = v
        private var cardContainer: View? = null
        private var cardContainerBack: LinearLayout? = null
        private var context: Context = context
        private var flipCard: LinearLayout? = null
        private var flipBackCard: LinearLayout? = null
        private var flipView: EasyFlipView? = null
        private var cardNickname: TextView? = null
        private var cardCVV: TextView? = null
        private var cardVaultColor: View? = null
        private var cardVaultColorBack: View? = null
        private var cardEditBtn: LinearLayout? = null

        private var cardFront: CardView? = null
        private var cardBack: CardView? = null

        init {
            cardType = view.findViewById(R.id.card_type)
            cardNumber = view.findViewById(R.id.card_number)
            cardExpiryDate = view.findViewById(R.id.expiry_date)
            cardholderName = view.findViewById(R.id.cardholder_name)
            cardContainer = view.findViewById(R.id.card_container)
            flipCard = view.findViewById(R.id.flip_card_btn)
            flipBackCard = view.findViewById(R.id.flip_card_back_btn)
            flipView = view.findViewById(R.id.flip_view)
            cardNickname = view.findViewById(R.id.card_nickname_tv)
            cardCVV = view.findViewById(R.id.card_cvv)
            cardContainerBack = view.findViewById(R.id.card_container_back)
            cardVaultColor = view.findViewById(R.id.card_vault_color)
            cardVaultColorBack = view.findViewById(R.id.card_vault_color_back)
            cardEditBtn = view.findViewById(R.id.edit_card)

            cardFront = view.findViewById(R.id.card_front)
            cardBack = view.findViewById(R.id.card_back)
        }

        fun bindCard(card: CreditCard, position: Int, listener: CreditCardClickListener){
            card.type = CreditCardRepository.getCreditCardType(card.number)

            val cardNumber = card.number.replace(" ","")
            val stringBuilder = StringBuilder(cardNumber)
            when(card.type) {
                CardType.Visa -> {
                    stringBuilder.insert(4 ," ")
                    stringBuilder.insert(9 ," ")
                    stringBuilder.insert(14 ," ")
                }
                CardType.Mastercard -> {
                    stringBuilder.insert(4 ," ")
                    stringBuilder.insert(9 ," ")
                    stringBuilder.insert(14 ," ")
                }
                CardType.Amex -> {
                    stringBuilder.insert(4 ," ")
                    stringBuilder.insert(11 ," ")
                }
                CardType.DiscoverCard -> {
                    stringBuilder.insert(4 ," ")
                    stringBuilder.insert(9 ," ")
                    stringBuilder.insert(14 ," ")
                }
                CardType.JCB -> {
                    stringBuilder.insert(4 ," ")
                    stringBuilder.insert(9 ," ")
                    stringBuilder.insert(14 ," ")
                }
                CardType.DinersClub -> {
                    stringBuilder.insert(4 ," ")
                    stringBuilder.insert(11 ," ")
                }
                CardType.Unknown -> {
                    stringBuilder.insert(4 ," ")
                    stringBuilder.insert(9 ," ")
                    stringBuilder.insert(14 ," ")
                }
            }

            this.cardNumber?.text = stringBuilder.toString()
            this.cardExpiryDate?.text = card.expirationDate
            this.cardholderName?.text = card.cardholderName.toUpperCase()
            this.cardNickname?.text = card.cardName
            this.cardCVV?.text = card.cvv

            val cardTypeImg = CreditCardRepository.getCreditCardTypeImage(card)
            if (cardTypeImg != null) {
                cardType?.setImageResource(cardTypeImg!!)
            } else {
                cardType?.visibility = View.GONE
            }

            val vault = VaultRepository.getVaultByID(card.vaultId)
            //cardVaultColor?.setBackgroundResource(vault?.getVaultColorResource() ?: R.color.colorPrimaryDark)
            //cardVaultColorBack?.setBackgroundResource(vault?.getVaultColorResource() ?: R.color.colorPrimaryDark)

            flipCard?.setOnClickListener { flipView?.flipTheView() }
            flipBackCard?.setOnClickListener { flipView?.flipTheView() }
            cardEditBtn?.setOnClickListener { listener.onCreditCardEditButtonClicked(card, position) }

            val width = cardFront?.measuredWidth
            val displayMetrics = context.resources.displayMetrics

            val screenWidth = displayMetrics.widthPixels
            val matchParentCardWidth = screenWidth - 32.dp
            var cardWidth = matchParentCardWidth
            if (cardWidth.px > 370) { cardWidth = 370.dp }
            val cardHeight = cardWidth / 1.58 + 8.dp

            val params =  RelativeLayout.LayoutParams(cardWidth, cardHeight.toInt())
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            params.bottomMargin = 8.dp
            cardFront?.layoutParams = params
            cardBack?.layoutParams = params
        }

    }
}