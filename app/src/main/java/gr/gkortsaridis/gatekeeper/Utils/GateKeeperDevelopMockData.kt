package gr.gkortsaridis.gatekeeper.Utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gr.gkortsaridis.gatekeeper.Entities.*

object GateKeeperDevelopMockData {
    val mockVault = Vault(id="10", account_id="1", name="Personal", color= VaultColor.Red, dateCreated=123L, dateModified=234L)
    val mockLogin = Login(account_id = "1", name = "Mock login", username = "gkortsaridis@gmail.com", password = "pass", url = "www.google.com", notes = "Some Notes", date_created=123L, date_modified=234L, vault_id= mockVault.id)
    val mockCard = CreditCard(cardName = "Personal Debit", type = CardType.Mastercard, number = "000 0000 0000 0000", expirationDate = "02/23", cvv = "0123", cardholderName = "John Doe", vaultId = "10", accountId = "1")
    val mockNote = Note(id="1", title = "Note title", body = "Note body\nNew Line\nThird Line", createDate = 123L, modifiedDate = 234L, accountId = "1", isPinned = true, vaultId = "10", color = NoteColor.Blue)
    val mockLogins = arrayListOf(mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin)
    val mockCards = arrayListOf(mockCard,mockCard,mockCard,mockCard,mockCard,mockCard,mockCard,mockCard,mockCard,mockCard,mockCard,mockCard,mockCard,mockCard,mockCard,mockCard,mockCard,mockCard,mockCard)
    val mockNotes = arrayListOf(mockNote,mockNote,mockNote,mockNote,mockNote,mockNote,mockNote,mockNote,mockNote,mockNote,mockNote,mockNote,mockNote,mockNote,mockNote,mockNote,mockNote,mockNote,mockNote)
    val mockLoginsLive = MutableLiveData<ArrayList<Login>>()
    val mockCardsLive = MutableLiveData<ArrayList<CreditCard>>()
    val mockNotesLive = MutableLiveData<ArrayList<Note>>()
    val mockNoLogins = arrayListOf<Login>()
}