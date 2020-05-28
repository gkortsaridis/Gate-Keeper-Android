package gr.gkortsaridis.gatekeeper.Entities.Network

data class RespUserData(val userId: String,
                        val extraDataEncryptedData: String,
                        val extraDataIv: String)