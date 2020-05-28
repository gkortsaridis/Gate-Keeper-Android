package gr.gkortsaridis.gatekeeper.Entities

import java.sql.Timestamp

data class UserLog(val id: Long = -1, var userId: String = "", val deviceId: Long = -1, val action: String = "", val timestamp: Timestamp = Timestamp(System.currentTimeMillis()))