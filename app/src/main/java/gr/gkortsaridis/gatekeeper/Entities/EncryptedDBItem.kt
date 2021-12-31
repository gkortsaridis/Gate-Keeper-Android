package gr.gkortsaridis.gatekeeper.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data")
data class EncryptedDBItem(
    @PrimaryKey
    val id: String = "-1",
    val type: Int = -1,
    val encryptedData: String,
    val iv: String,
    val dateCreated: Long? = null,
    val dateModified: Long? = null
)