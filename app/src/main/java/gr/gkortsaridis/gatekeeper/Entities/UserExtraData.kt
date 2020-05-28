package gr.gkortsaridis.gatekeeper.Entities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64


data class UserExtraData(val userEmail: String, var userFullName: String?, var userImg: String?, var gateKeeperSubscriptionStatus: GateKeeperSubscriptionStatus?) {

    fun getUserImgBmp() : Bitmap? {
        return try {
            if (userImg != null && userImg != "") {
                val userImgBytes = Base64.decode(userImg, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(userImgBytes, 0, userImgBytes.size)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}