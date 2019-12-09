package gr.gkortsaridis.gatekeeper.Entities

import com.google.gson.Gson
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import java.io.Serializable
import java.util.concurrent.CompletableFuture

enum class CardType {
    Mastercard,
    Visa,
    Amex,
    DiscoverCard,
    DinersClub
}

enum class Bank {
    Monzo,
    HSBC,
    Barclays,
    Lloyds
}

class CreditCard: Serializable {

    var id: String
    var cardName: String
    var type: CardType
    var bank: Bank
    var number: String
    var expirationDate: String
    var cvv: String
    var cardholderName: String
    var accountId: String

    constructor(cardName: String,bank: Bank, type: CardType, number: String, expirationDate: String, cvv: String, cardholderName: String, accountId: String) {
        this.id = "-1"
        this.cardName = cardName
        this.bank = bank
        this.type = type
        this.number = number
        this.expirationDate = expirationDate
        this.cvv = cvv
        this.cardholderName = cardholderName
        this.accountId = accountId
    }

    fun encrypt() : String {
        val decrypted = Gson().toJson(this)
        val response = CompletableFuture<String>()
        ECSymmetric().encrypt(decrypted, AuthRepository.getUserID(), object :
            ECResultListener {
            override fun onFailure(message: String, e: Exception) {
                response.complete("-1")
            }

            override fun <T> onSuccess(result: T) {
                response.complete(result as String)
            }
        })

        return response.get()
    }

}