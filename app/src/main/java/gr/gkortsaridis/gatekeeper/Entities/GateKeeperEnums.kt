package gr.gkortsaridis.gatekeeper.Entities

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
    White("#ffffff")
}

enum class LoginUrlType(val value: String) {
    Web("Web"),
    App("App")
}