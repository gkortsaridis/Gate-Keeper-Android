package gr.gkortsaridis.gatekeeper.Entities

data class EncryptedData(val id: Long = -1, val encryptedData: String, val iv: String)