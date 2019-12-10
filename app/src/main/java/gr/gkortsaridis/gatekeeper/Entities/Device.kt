package gr.gkortsaridis.gatekeeper.Entities

import com.google.firebase.Timestamp

data class Device(val OS : String,
                  val version : String,
                  val versionNum: Int,
                  val UID : String,
                  val vendor: String,
                  val nickname: String,
                  var locale: String,
                  val firstAdded: Timestamp,
                  var lastEntry: Timestamp)