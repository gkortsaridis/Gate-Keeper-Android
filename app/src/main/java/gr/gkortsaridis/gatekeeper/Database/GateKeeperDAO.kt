package gr.gkortsaridis.gatekeeper.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.Vault

@Dao
interface GateKeeperDAO {

    /*
    //Get data on the main thread
    @get:Query("SELECT * FROM logins")
    val allLoginsSync: List<Login>

    @get:Query("SELECT * FROM vaults")
    val allVaultsSync: List<Vault>

    @get:Query("SELECT * FROM cards")
    val allCardsSync: List<CreditCard>

    @get:Query("SELECT * FROM notes")
    val allNotesSync: List<Note>*/

    //Use LiveData to get stuff from DB
    @get:Query("SELECT * FROM logins")
    val allLogins: LiveData<List<Login>>

    @get:Query("SELECT * FROM vaults")
    val allVaults: LiveData<List<Vault>>

    @get:Query("SELECT * FROM cards")
    val allCards: LiveData<List<CreditCard>>

    @get:Query("SELECT * FROM notes")
    val allNotes: LiveData<List<Note>>

    //Inserts
    @Insert
    fun insertLogin(login: Login)

    @Insert
    fun insertVault(vault: Vault)

    @Insert
    fun insertCard(card: CreditCard)

    @Insert
    fun insertNote(note: Note)

    //Updates
    @Update
    fun updateLogin(login: Login)

    @Update
    fun updateVault(vault: Vault)

    @Update
    fun updateCard(card: CreditCard)

    @Update
    fun updateNote(note: Note)

    //Deletes
    @Delete
    fun deleteLogin(login: Login)

    @Delete
    fun deleteVault(vault: Vault)

    @Delete
    fun deleteCard(card: CreditCard)

    @Delete
    fun deleteNote(note: Note)


    //Queries
    @Query("SELECT * FROM logins WHERE id = :id")
    fun loadLoginById(id: String): Login?

    @Query("SELECT * FROM vaults WHERE id = :id")
    fun loadVaultById(id: String): Vault?

    @Query("SELECT * FROM cards WHERE id = :id")
    fun loadCardById(id: String): CreditCard?

    @Query("SELECT * FROM notes WHERE id = :id")
    fun loadNoteById(id: String): Note?

    @Query("DELETE FROM logins")
    fun truncateLogins()

    @Query("DELETE FROM vaults")
    fun truncateVaults()

    @Query("DELETE FROM cards")
    fun truncateCards()

    @Query("DELETE FROM notes")
    fun truncateNotes()
}