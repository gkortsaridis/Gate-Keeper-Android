package gr.gkortsaridis.gatekeeper.Entities

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme

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

    fun getVaultColorResource(): Color {
        return when(color) {
            VaultColor.White -> GateKeeperTheme.vault_white_2
            VaultColor.Yellow -> GateKeeperTheme.vault_yellow_1
            VaultColor.Red -> GateKeeperTheme.vault_red_1
            VaultColor.Green -> GateKeeperTheme.vault_green_1
            VaultColor.Blue -> GateKeeperTheme.vault_blue_1
            VaultColor.Coral -> GateKeeperTheme.vault_coral
            else -> GateKeeperTheme.colorPrimaryDark
        }
    }

    fun getVaultColorAccent(): Color {
        return when(color) {
            VaultColor.White -> GateKeeperTheme.mate_black
            VaultColor.Yellow -> GateKeeperTheme.mate_black
            VaultColor.Red -> GateKeeperTheme.white
            VaultColor.Green -> GateKeeperTheme.white
            VaultColor.Blue -> GateKeeperTheme.white
            VaultColor.Coral -> GateKeeperTheme.white
            else -> GateKeeperTheme.colorPrimaryDark
        }
    }

}