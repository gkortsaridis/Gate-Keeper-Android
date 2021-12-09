package gr.gkortsaridis.gatekeeper.Entities

import java.sql.Timestamp

data class EncryptedData(val id: String = "-1", val encryptedData: String, val iv: String, val dateCreated: Long? = null, val dateModified: Long? = null)