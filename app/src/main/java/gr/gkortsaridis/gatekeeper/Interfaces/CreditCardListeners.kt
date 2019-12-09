package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import java.lang.Exception

interface CreditCardClickListener {
}

interface CreditCardRetrieveListener {
    fun onCreditCardsReceived(cards: ArrayList<CreditCard>)
    fun onCreditCardsReceiveError(e: Exception)
}

interface CreditCardCreateListener {
    fun onCreditCardCreated()
    fun onCreditCardCreateError()
}