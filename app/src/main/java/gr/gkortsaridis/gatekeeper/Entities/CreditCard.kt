package gr.gkortsaridis.gatekeeper.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.type.DateTime
import java.io.Serializable
import java.sql.Timestamp

@Entity(tableName = "cards")
data class CreditCard(
    @PrimaryKey
    var id: String = "-1",
    var cardName: String,
    var type: CardType,
    var number: String,
    var expirationDate: String,
    var cvv: String,
    var cardholderName: String,
    var vaultId: String,
    var accountId: String,
    var modifiedDate: Long? = null,
    var createdDate: Long? = null
): Serializable