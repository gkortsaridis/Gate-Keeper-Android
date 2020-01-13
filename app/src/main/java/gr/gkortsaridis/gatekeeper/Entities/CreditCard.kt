package gr.gkortsaridis.gatekeeper.Entities

import java.io.Serializable

data class CreditCard( var id: String = "-1",
                       var cardName: String,
                       var type: CardType,
                       var number: String,
                       var expirationDate: String,
                       var cvv: String,
                       var cardholderName: String,
                       var accountId: String): Serializable