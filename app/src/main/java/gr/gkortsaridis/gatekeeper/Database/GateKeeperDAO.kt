package gr.gkortsaridis.gatekeeper.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import gr.gkortsaridis.gatekeeper.Entities.EncryptedDBItem

@Dao
interface GateKeeperDAO {

    //~~~~~~~~ READ DATA ~~~~~~~~
    @get:Query("SELECT * FROM data WHERE type = 0")
    val allVaults: List<EncryptedDBItem>

    @get:Query("SELECT * FROM data")
    val allData: List<EncryptedDBItem>

    @get:Query("SELECT * FROM data WHERE type = 1")
    val allLogins: List<EncryptedDBItem>

    @get:Query("SELECT * FROM data WHERE type = 2")
    val allCards: List<EncryptedDBItem>

    @get:Query("SELECT * FROM data WHERE type = 3")
    val allNotes: List<EncryptedDBItem>

    @get:Query("SELECT * FROM data WHERE type = 4")
    val allDevices: List<EncryptedDBItem>

    //~~~~~~~~ READ LIVE DATA ~~~~~~~~
    @get:Query("SELECT * FROM data WHERE type = 0")
    val allVaultsLive: LiveData<List<EncryptedDBItem>>

    @get:Query("SELECT * FROM data WHERE type = 1")
    val allLoginsLive: LiveData<List<EncryptedDBItem>>

    @get:Query("SELECT * FROM data WHERE type = 2")
    val allCardsLive: LiveData<List<EncryptedDBItem>>

    @get:Query("SELECT * FROM data WHERE type = 3")
    val allNotesLive: LiveData<List<EncryptedDBItem>>

    //~~~~~~~~ QUERY DATA ~~~~~~~~
    @Query("SELECT * FROM data WHERE type = 0 AND id = :id")
    fun loadVaultById(id: String): EncryptedDBItem?

    @Query("SELECT * FROM data WHERE type = 1 AND id = :id")
    fun loadLoginById(id: String): EncryptedDBItem?

    @Query("SELECT * FROM data WHERE type = 2 AND id = :id")
    fun loadCardById(id: String): EncryptedDBItem?

    @Query("SELECT * FROM data WHERE type = 3 AND id = :id")
    fun loadNoteById(id: String): EncryptedDBItem?

    @Query("SELECT * FROM data WHERE type = 4 AND id = :id")
    fun loadDeviceById(id: String): EncryptedDBItem?


    //~~~~~~~~ INSERT DATA ~~~~~~~~
    @Insert
    fun insertUserData(dbItems: ArrayList<EncryptedDBItem>)

    @Insert
    fun insertSingleDataObject(dbItem: EncryptedDBItem)

    //~~~~~~~~ UPDATE DATA ~~~~~~~~
    @Update
    fun updateSingleDataObject(dbItem: EncryptedDBItem)

    //~~~~~~~~ DELETE DATA ~~~~~~~~
    @Query("DELETE FROM data")
    fun deleteAllData()

    @Query("DELETE FROM data WHERE type = 1 AND id = :id")
    fun deleteLoginById(id: String)

}