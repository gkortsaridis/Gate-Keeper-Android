package gr.gkortsaridis.gatekeeper.Entities

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

enum class NoteColor(val value: String) {
    Red("#ff0000"),
    Yellow("#ffff00"),
    Blue("#0000ff"),
    White("#fafafa")
}