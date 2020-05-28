package gr.gkortsaridis.gatekeeper.Entities

import java.sql.Timestamp

data class EncryptedData(val id: Long = -1, val encryptedData: String, val iv: String, val dateCreated: Timestamp? = null, val dateModified: Timestamp? = null)