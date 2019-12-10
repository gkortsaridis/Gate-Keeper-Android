package gr.gkortsaridis.gatekeeper.Repositories

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.DevicesRetrieveListener
import java.util.*
import kotlin.collections.ArrayList


object DeviceRepository {

    private const val TAG = "_DEVICE_REPOSITORY_"

    fun retrieveDevicesByAccountID(accountID: String, retrieveListener: DevicesRetrieveListener) {

        val db = FirebaseFirestore.getInstance()
        db.collection("devices")
            .whereEqualTo("account_id",accountID)
            .get().addOnSuccessListener { result ->
                val devicesResult = ArrayList<Device>()

                for (document in result) {
                    val encryptedDevice = document["device"] as String
                    val decryptedDevice = Gson().fromJson(encryptedDevice, Device::class.java)
                    if (decryptedDevice != null){
                        devicesResult.add(decryptedDevice)
                    }
                }

                retrieveListener.onDevicesRetrieved(devicesResult)
            }
            .addOnFailureListener { exception -> retrieveListener.onDeviceRetrieveError(exception) }
    }

    fun logCurrentLogin(context: Context) {
        //val encryptedDevice = SecurityRepository.encryptObject(getCurrentDevice())

        val currentDevice = getCurrentDevice(context)
        currentDevice.lastEntry = Date()
        currentDevice.locale = getDetectedCountry(context, "??")

        val devicehash = hashMapOf(
            "device" to Gson().toJson(currentDevice),
            "account_id" to AuthRepository.getUserID()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("devices")
            .document(currentDevice.UID)
            .set(devicehash)
            .addOnCompleteListener { Log.i(TAG, "Login was logged") }
    }

    private fun getCurrentDevice(context: Context): Device {

        val UID = FirebaseInstanceId.getInstance().id
        val OS = "Android"
        val version = Build.VERSION.CODENAME
        val versionNum = Build.VERSION.SDK_INT
        val vendor = Build.MANUFACTURER + " " +Build.MODEL
        val nickname = ""
        val locale = getDetectedCountry(context, "??")

        val devices = GateKeeperApplication.devices

        for (device in devices ?: ArrayList()) { if (device.UID == UID) { return device } }

        return Device(OS= OS, version = version, versionNum = versionNum, UID = UID, vendor = vendor, nickname = nickname, locale = locale, firstAdded = Date(), lastEntry = Date())
    }

    private fun getDetectedCountry(context: Context, defaultCountryIsoCode: String): String {
        detectSIMCountry(context)?.let {
            return it
        }

        detectNetworkCountry(context)?.let {
            return it
        }

        detectLocaleCountry(context)?.let {
            return it
        }

        return defaultCountryIsoCode
    }

    private fun detectSIMCountry(context: Context): String? {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            Log.d(TAG, "detectSIMCountry: ${telephonyManager.simCountryIso}")
            return telephonyManager.simCountryIso
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun detectNetworkCountry(context: Context): String? {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            Log.d(TAG, "detectNetworkCountry: ${telephonyManager.simCountryIso}")
            return telephonyManager.networkCountryIso
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun detectLocaleCountry(context: Context): String? {
        try {
            val localeCountryISO = context.resources.configuration.locale.country
            Log.d(TAG, "detectNetworkCountry: $localeCountryISO")
            return localeCountryISO
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}