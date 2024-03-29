package gr.gkortsaridis.gatekeeper.Repositories

import android.content.Context
import android.os.Build
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import android.util.Log
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import java.sql.Timestamp


object DeviceRepository {

    private const val TAG = "_DEVICE_REPOSITORY_"

    fun getDeviceById(id: String): Device? {
        return GateKeeperApplication.devices?.find { it.id == id }
    }

    fun getLastUsedDatetime(device: Device): Timestamp? {
        val og = GateKeeperApplication.userLog
        val userLog = GateKeeperApplication.userLog.filter { it.deviceId == device.id?.toLong() }
        return if (userLog.isNotEmpty()) userLog[userLog.size-1].timestamp else null
    }

    fun getCurrentDevice(context: Context): Device {

        val fields = Build.VERSION_CODES::class.java.fields
        var codeName = "UNKNOWN"
        fields.filter { it.getInt(Build.VERSION_CODES::class) == Build.VERSION.SDK_INT }.forEach { codeName = it.name }

        val UID = Secure.getString(context.contentResolver, Secure.ANDROID_ID)
        val OS = "Android"
        val version = codeName
        val versionNum = Build.VERSION.SDK_INT
        val vendor = Build.MANUFACTURER + " " +Build.MODEL
        val nickname = ""
        val locale = getDetectedCountry(context, "??")

        val devices = GateKeeperApplication.devices
        for (device in devices ?: ArrayList()) { if (device.UID == UID) { return device } }

        return Device(OS= OS, version = version, versionNum = versionNum, UID = UID, vendor = vendor, nickname = nickname, locale = locale, isTablet = context.resources.getBoolean(
            R.bool.isTablet))
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