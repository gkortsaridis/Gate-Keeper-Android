package gr.gkortsaridis.gatekeeper.Entities.Network

import gr.gkortsaridis.gatekeeper.Entities.UserLog

data class RespLogs(val errorCode: Int, val errorMsg: String?, val data: ArrayList<UserLog>?)