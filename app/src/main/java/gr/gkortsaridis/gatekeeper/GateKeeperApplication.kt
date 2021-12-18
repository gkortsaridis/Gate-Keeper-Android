package gr.gkortsaridis.gatekeeper

import android.app.Application
import com.bugsnag.android.Bugsnag
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.revenuecat.purchases.PurchaserInfo
import com.revenuecat.purchases.Purchases
import dagger.hilt.android.HiltAndroidApp
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository


@HiltAndroidApp
class GateKeeperApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        user_id = DataRepository.savedUser
        //MobileAds.initialize(this, admobAppID)

        Bugsnag.start(this)
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
    }

    companion object {

        lateinit var networkFlipperPlugin: NetworkFlipperPlugin
        lateinit var instance: GateKeeperApplication private set
        var user_id: String? = null
        val admobAppID = "ca-app-pub-4492385836648698~3680446633"


        var vaults: ArrayList<Vault> = arrayListOf()
        var logins: ArrayList<Login> = arrayListOf()
        var cards: ArrayList<CreditCard> = arrayListOf()
        var notes: ArrayList<Note> = arrayListOf()

        var extraData: UserExtraData? = null
        var devices: ArrayList<Device>? = null
        var purchaserInfo: PurchaserInfo? = null
        var userLog: ArrayList<UserLog> = arrayListOf()
    }

}