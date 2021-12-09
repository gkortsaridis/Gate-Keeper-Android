package gr.gkortsaridis.gatekeeper.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.type.DateTime
import gr.gkortsaridis.gatekeeper.R
import java.sql.Timestamp

@Entity(tableName = "vaults")
data class Vault(
    @PrimaryKey
    var id: String = "-1",
    var account_id : String,
    var name : String,
    var color: VaultColor?,
    var dateCreated: Long? = null,
    var dateModified: Long? = null
) {

    fun getVaultColorResource(): Int {
        return when(color) {
            VaultColor.White -> R.color.vault_white_2
            VaultColor.Yellow -> R.color.vault_yellow_1
            VaultColor.Red -> R.color.vault_red_1
            VaultColor.Green -> R.color.vault_green_1
            VaultColor.Blue -> R.color.vault_blue_1
            VaultColor.Coral -> R.color.vault_coral
            else -> R.color.colorPrimaryDark
        }
    }

    fun getVaultColorAccent(): Int {
        return when(color) {
            VaultColor.White -> R.color.mate_black
            VaultColor.Yellow -> R.color.mate_black
            VaultColor.Red -> R.color.white
            VaultColor.Green -> R.color.white
            VaultColor.Blue -> R.color.white
            VaultColor.Coral -> R.color.white
            else -> R.color.colorPrimaryDark
        }
    }

}