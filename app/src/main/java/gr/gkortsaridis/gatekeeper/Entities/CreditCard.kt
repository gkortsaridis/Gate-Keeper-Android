package gr.gkortsaridis.gatekeeper.Entities

import java.io.Serializable
import java.sql.Timestamp

data class CreditCard( var id: String = "-1",
                       var cardName: String,
                       var type: CardType,
                       var number: String,
                       var expirationDate: String,
                       var cvv: String,
                       var cardholderName: String,
                       var vaultId: String,
                       var accountId: String,
                       var modifiedDate: Timestamp? = null,
                       var createdDate: Timestamp? = null
): Serializable