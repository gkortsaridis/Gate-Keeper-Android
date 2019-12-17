package gr.gkortsaridis.gatekeeper.Entities

import android.text.format.DateUtils
import com.google.firebase.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat

data class Device(val OS : String,
                  val version : String,
                  val versionNum: Int,
                  val UID : String,
                  val vendor: String,
                  var nickname: String,
                  var locale: String,
                  val firstAdded: Timestamp,
                  var lastEntry: Timestamp){

    fun formattedDate(): String {
        val lastEntryDate = lastEntry.toDate()
        val sdf: DateFormat?
        val dateStr: String
        when {
            DateUtils.isToday(lastEntryDate.time) -> {
                sdf = SimpleDateFormat("hh:mm")
                dateStr = "Today, "+sdf.format(lastEntryDate)
            }
            DateUtils.isToday(lastEntryDate.time + DateUtils.DAY_IN_MILLIS) -> {
                sdf = SimpleDateFormat("hh:mm")
                dateStr = "Yesterday, "+sdf.format(lastEntryDate)
            }
            else -> {
                sdf = SimpleDateFormat("MM/dd/yyyy hh:mm")
                dateStr = sdf.format(lastEntryDate)
            }
        }

        return dateStr
    }

}