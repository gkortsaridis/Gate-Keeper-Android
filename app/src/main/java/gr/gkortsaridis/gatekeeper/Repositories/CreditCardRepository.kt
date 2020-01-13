package gr.gkortsaridis.gatekeeper.Repositories

import android.app.Activity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardDeleteListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardUpdateListener

object CreditCardRepository {

    fun getCreditCardType(cardNumber: String): CardType {
        if (cardNumber.isNotEmpty()) {
            if (cardNumber.length > 1) {
                if (cardNumber.substring(0,2) == "37") { return CardType.Amex }
                else if (cardNumber.substring(0,2) == "38") { return CardType.DinersClub }
            }

            return when {
                cardNumber[0] == '4' -> CardType.Visa
                cardNumber[0] == '5' -> CardType.Mastercard
                cardNumber[0] == '6' -> CardType.DiscoverCard
                else -> CardType.Unknown
            }

        }else { return CardType.Unknown }

    }

    fun deleteCreditCard(card: CreditCard, listener: CreditCardDeleteListener) {
        val db = FirebaseFirestore.getInstance()
        db.collection("cards")
            .document(card.id)
            .delete()
            .addOnCompleteListener {
                listener.onCardDeleted()
            }
    }

    fun updateCreditCard(card: CreditCard, listener: CreditCardUpdateListener) {
        val cardhash = hashMapOf(
            "card" to SecurityRepository.encryptObjectWithUserCredentials(card),
            "account_id" to AuthRepository.getUserID()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("cards")
            .document(card.id)
            .set(cardhash)
            .addOnCompleteListener {
                listener.onCardUpdated(card)
            }
    }

    fun encryptAndStoreCard(activity: Activity, card: CreditCard, listener: CreditCardCreateListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()

        val encryptedCard = SecurityRepository.encryptObjectWithUserCredentials(card)

        val cardhash = hashMapOf(
            "card" to encryptedCard,
            "account_id" to AuthRepository.getUserID()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("cards")
            .add(cardhash)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    viewDialog.hideDialog()
                    card.id = it.result?.id ?: ""
                    listener.onCreditCardCreated(card)
                }
                else {
                    viewDialog.hideDialog()
                    listener.onCreditCardCreateError()
                }
            }
    }

    fun retrieveCardsByAccountID(accountID: String, retrieveListener: CreditCardRetrieveListener) {
        val db = FirebaseFirestore.getInstance()
        db.collection("cards")
            .whereEqualTo("account_id",accountID)
            .get().addOnSuccessListener { result ->
                val cardsResult = ArrayList<CreditCard>()

                val encryptedCardsToSaveLocally = ArrayList<String>()

                for (document in result) {
                    val encryptedCard = document["card"] as String
                    encryptedCardsToSaveLocally.add(encryptedCard)
                    val decryptedCard = SecurityRepository.decryptStringToObjectWithUserCredentials(encryptedCard, CreditCard::class.java) as CreditCard?
                    if (decryptedCard != null){
                        decryptedCard.id = document.id
                        cardsResult.add(decryptedCard)
                    }
                }

                //Save cards locally
                DataRepository.savedCards = Gson().toJson(encryptedCardsToSaveLocally)

                retrieveListener.onCreditCardsReceived(cardsResult)
            }
            .addOnFailureListener { exception -> retrieveListener.onCreditCardsReceiveError(exception) }
    }

    fun getCreditCardById(cardId: String): CreditCard? {
        for (card in GateKeeperApplication.cards) {
            if (card.id == cardId) {
                return card
            }
        }
        return null
    }

}