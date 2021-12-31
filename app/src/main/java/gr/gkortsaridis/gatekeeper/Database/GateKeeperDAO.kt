package gr.gkortsaridis.gatekeeper.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import gr.gkortsaridis.gatekeeper.Entities.*

@Dao
interface GateKeeperDAO {

    //Use LiveData to get stuff from DB
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

    @get:Query("SELECT * FROM data WHERE type = 0")
    val allVaults: List<EncryptedDBItem>

    @Query("SELECT * FROM data WHERE type = 1 AND id = :id")
    fun loadLoginById(id: String): EncryptedDBItem?

    @Query("SELECT * FROM data WHERE type = 0 AND id = :id")
    fun loadVaultById(id: String): EncryptedDBItem?

    @Query("SELECT * FROM data WHERE type = 2 AND id = :id")
    fun loadCardById(id: String): EncryptedDBItem?

    @Query("SELECT * FROM data WHERE type = 3 AND id = :id")
    fun loadNoteById(id: String): EncryptedDBItem?

    @Query("SELECT * FROM data WHERE type = 4 AND id = :id")
    fun loadDeviceById(id: String): EncryptedDBItem?


    //Inserts
    @Insert
    fun insertUserData(dbItems: ArrayList<EncryptedDBItem>)

    //Deletes
    @Query("DELETE FROM data")
    fun deleteAllData()
}