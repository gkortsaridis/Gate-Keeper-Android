package gr.gkortsaridis.gatekeeper

import android.app.Application
import android.util.Log
import com.android.billingclient.api.*
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseUser
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository.PREFS_FILENAME

class GateKeeperApplication : Application(), PurchasesUpdatedListener {

    lateinit private var billingClient: BillingClient

    override fun onCreate() {
        super.onCreate()
        instance = this
        user_id = DataRepository.savedUser
        MobileAds.initialize(this, admobAppID)

        SoLoader.init(this, false)
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(SharedPreferencesFlipperPlugin(this, PREFS_FILENAME))
            client.addPlugin(NavigationFlipperPlugin.getInstance())
            client.addPlugin(NetworkFlipperPlugin())

            client.start()
        }


        /*billingClient = BillingClient
            .newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                }

                Log.i("ON BILLING SETUP FINISHED", billingResult.toString())

            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })*/
    }

    companion object {
        lateinit var instance: GateKeeperApplication private set
        var user: FirebaseUser? = null
        var user_id: String? = null
        val admobAppID = "ca-app-pub-4492385836648698~3680446633"

        lateinit var logins: ArrayList<Login>
        lateinit var vaults: ArrayList<Vault>
        lateinit var cards: ArrayList<CreditCard>
        lateinit var notes: ArrayList<Note>
        var devices: ArrayList<Device>? = null
    }

    override fun onPurchasesUpdated(p0: BillingResult?, p1: MutableList<Purchase>?) {
        Log.i("ON PURCHASES UPDATED", p0.toString())

    }


}