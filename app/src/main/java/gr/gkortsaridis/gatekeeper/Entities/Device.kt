package gr.gkortsaridis.gatekeeper.Entities

import java.util.*

data class Device(val OS : String,
                  val version : String,
                  val versionNum: Int,
                  val UID : String,
                  val vendor: String,
                  val nickname: String,
                  var locale: String,
                  val firstAdded: Date,
                  var lastEntry: Date)