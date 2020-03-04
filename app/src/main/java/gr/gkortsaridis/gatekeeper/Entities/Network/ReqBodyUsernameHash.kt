package gr.gkortsaridis.gatekeeper.Entities.Network

data class ReqBodyUsernameHash(val username: String, val hash: String, val deviceEncryptedData: String, val deviceIv: String, val deviceUid: String)