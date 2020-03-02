package gr.gkortsaridis.gatekeeper.Entities.Network

data class ReqBodyEncryptedData(val userId: String, val encryptedData: String, val iv: String, val username: String, val hash: String)