package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.Vault

interface SearchResultClickListener {
    fun onVaultClicked(vault: Vault)
    fun onLoginClicked(login: Login)
    fun onCardClicked(card: CreditCard)
    fun onNoteClicked(note: Note)
}