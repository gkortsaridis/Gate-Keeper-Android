package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import java.lang.Exception

interface CreditCardClickListener {
    fun onCreditCardClicked(card: CreditCard)
    fun onCreditCardEditButtonClicked(card:CreditCard, position: Int)
}

interface CreditCardRetrieveListener {
    fun onCreditCardsReceived(cards: ArrayList<CreditCard>)
    fun onCreditCardsReceiveError(e: Exception)
}

interface CreditCardCreateListener {
    fun onCreditCardCreated(card: CreditCard)
    fun onCreditCardCreateError(errorCode: Int, errorMsg: String) {}
}

interface CreditCardDeleteListener {
    fun onCardDeleted()
    fun onCardDeleteError(errorCode: Int, errorMsg: String){}
}

interface CreditCardUpdateListener {
    fun onCardUpdated(card: CreditCard)
    fun onCardUpdateError(errorCode: Int, errorMsg: String){}
}