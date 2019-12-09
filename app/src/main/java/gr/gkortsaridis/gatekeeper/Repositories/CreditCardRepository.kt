package gr.gkortsaridis.gatekeeper.Repositories

import android.app.Activity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginRetrieveListener
import java.util.concurrent.CompletableFuture

object CreditCardRepository {

    fun encryptAndStoreCard(activity: Activity, card: CreditCard, listener: CreditCardCreateListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()

        val encryptedCard = card.encrypt()

        val loginhash = hashMapOf(
            "card" to encryptedCard,
            "account_id" to AuthRepository.getUserID()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("cards")
            .add(loginhash)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    viewDialog.hideDialog()
                    listener.onCreditCardCreated()
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
                    val decryptedCard = decryptCreditCard(encryptedCard)
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

    private fun decryptCreditCard(encryptedData: String): CreditCard? {
        val response = CompletableFuture<CreditCard>()
        val userId = AuthRepository.getUserID()

        if (userId != "") {
            ECSymmetric().decrypt(encryptedData, userId, object :
                ECResultListener {
                override fun onFailure(message: String, e: Exception) {
                    response.complete(null)
                }

                override fun <T> onSuccess(result: T) {
                    response.complete(Gson().fromJson(result.toString(), CreditCard::class.java))
                }
            })

            return response.get()
        }else {
            return null
        }

    }
}