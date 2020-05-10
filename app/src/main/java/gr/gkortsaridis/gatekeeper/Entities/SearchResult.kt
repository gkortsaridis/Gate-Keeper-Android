package gr.gkortsaridis.gatekeeper.Entities

import kotlin.math.log

enum class SearchResultType {
    LOGIN,
    CARD,
    NOTE,
    VAULT
}

data class SearchResult(
    val itemType: SearchResultType,
    val login: Login? = null,
    val card: CreditCard? = null,
    val note: Note? = null,
    val vault: Vault? = null
)