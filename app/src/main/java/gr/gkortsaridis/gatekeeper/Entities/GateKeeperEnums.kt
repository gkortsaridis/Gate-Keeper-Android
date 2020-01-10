package gr.gkortsaridis.gatekeeper.Entities

enum class CardType {
    Mastercard,
    Visa,
    Amex,
    DiscoverCard,
    DinersClub,
    Unknown
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
    White("#fafafa")
}

enum class LoginUrlType(val value: String) {
    Web("Web"),
    App("App")
}