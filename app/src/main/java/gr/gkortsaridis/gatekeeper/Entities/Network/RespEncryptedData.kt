package gr.gkortsaridis.gatekeeper.Entities.Network

import gr.gkortsaridis.gatekeeper.Entities.EncryptedData

data class RespEncryptedData(val errorCode: Int, val errorMsg: String, val data: EncryptedData)