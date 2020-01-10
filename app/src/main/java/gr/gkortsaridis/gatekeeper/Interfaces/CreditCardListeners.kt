package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import java.lang.Exception

interface CreditCardClickListener {
    fun onCreditCardClicked(card: CreditCard)
}

interface CreditCardRetrieveListener {
    fun onCreditCardsReceived(cards: ArrayList<CreditCard>)
    fun onCreditCardsReceiveError(e: Exception)
}

interface CreditCardCreateListener {
    fun onCreditCardCreated(card: CreditCard)
    fun onCreditCardCreateError()
}