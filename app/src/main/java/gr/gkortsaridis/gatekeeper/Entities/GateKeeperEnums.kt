package gr.gkortsaridis.gatekeeper.Entities

import androidx.compose.ui.graphics.Color
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme

enum class CardType(val value: String) {
    Mastercard("MASTERCARD"),
    Visa("VISA"),
    Amex("AMEX"),
    DiscoverCard("DISCOVER"),
    DinersClub("DINERS"),
    JCB("JCB"),
    Unknown("")
}

enum class Bank {
    Monzo,
    HSBC,
    Barclays,
    Lloyds
}

enum class NoteColor(val value: String) {
    Blue("#A7D9D6"),
    Cream("#FDE79D"),
    Green("#82CC74"),
    Orange("#F66522"),
    Pink("#F0A1A6"),
    Red("#F65A34"),
    Yellow("#FEF735"),
    White("#ffffff")
}

enum class VaultColor(val value: String) {
    Blue("#A7D9D6"),
    Cream("#FDE79D"),
    Green("#82CC74"),
    Orange("#F66522"),
    Pink("#F0A1A6"),
    Red("#F65A34"),
    Yellow("#FEF735"),
    White("#ffffff"),
    Coral("#47A8BD")
}

fun VaultColor.toColor(): Color {
    return when (this) {
        VaultColor.Red -> { GateKeeperTheme.vault_red_1 }
        VaultColor.Green -> { GateKeeperTheme.vault_green_1 }
        VaultColor.Blue -> { GateKeeperTheme.vault_blue_1 }
        VaultColor.Yellow -> { GateKeeperTheme.vault_yellow_1 }
        VaultColor.White -> { GateKeeperTheme.vault_white_1 }
        else -> {GateKeeperTheme.white }
    }
}

enum class LoginUrlType(val value: String) {
    Web("Web"),
    App("App")
}