package gr.gkortsaridis.gatekeeper.Entities

import gr.gkortsaridis.gatekeeper.R
import java.sql.Timestamp

data class Vault( var id: String = "-1",
                  var account_id : String,
                  var name : String,
                  var color: VaultColor?,
                  var dateCreated: Timestamp? = null,
                  var dateModified: Timestamp? = null) {

    fun getVaultColorResource(): Int {
        return when(color) {
            VaultColor.White -> R.color.white
            VaultColor.Yellow -> R.color.vault_yellow_1
            VaultColor.Red -> R.color.vault_red_1
            VaultColor.Green -> R.color.vault_green_1
            VaultColor.Blue -> R.color.vault_blue_1
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
            else -> R.color.colorPrimaryDark
        }
    }

}