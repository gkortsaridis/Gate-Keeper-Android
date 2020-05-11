package gr.gkortsaridis.gatekeeper

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.google.android.gms.ads.MobileAds
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.revenuecat.purchases.Purchases
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.Entities.UserExtraData
import gr.gkortsaridis.gatekeeper.Entities.UserLog
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository


class GateKeeperApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        user_id = DataRepository.savedUser
        MobileAds.initialize(this, admobAppID)

        networkFlipperPlugin = NetworkFlipperPlugin()

        SoLoader.init(this, false)
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(SharedPreferencesFlipperPlugin(this))
            client.addPlugin(NavigationFlipperPlugin.getInstance())
            client.addPlugin(DatabasesFlipperPlugin(this));

            client.addPlugin(networkFlipperPlugin)

            client.start()
        }

        //Setup RevenueCat SDK
        Purchases.debugLogsEnabled = true
        Purchases.configure(this, "SxHzqGfKRGokuExLiJOYdElknSsFMtwB")

        /*Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                Log.i("REVENUECAT ERROR", error.toString())
                /* Optional error handling */
            },
            onSuccess = { offerings ->
                offerings.current?.availablePackages?.takeUnless { it.isNullOrEmpty() }?.let {
                    // Display packages for sale
                    Log.i("REVENUECAT SUCCESS", it.toString())
                }
            }
        )*/

    }

    companion object {

        lateinit var networkFlipperPlugin: NetworkFlipperPlugin
        lateinit var instance: GateKeeperApplication private set
        var user_id: String? = null
        val admobAppID = "ca-app-pub-4492385836648698~3680446633"

        var extraData: UserExtraData? = null
        var devices: ArrayList<Device>? = null
        var userLog: ArrayList<UserLog> = arrayListOf()
    }

}