package gr.gkortsaridis.gatekeeper.Entities.Network

data class ReqBodyExtraDataUpdate(val username: String,
                                  val hash: String,
                                  val extrasEncryptedData: String,
                                  val extrasIv: String,
                                  val deviceEncryptedData: String,
                                  val deviceIv: String,
                                  val deviceUid: String)