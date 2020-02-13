package gr.gkortsaridis.gatekeeper

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseUser
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository

class GateKeeperApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        user_id = DataRepository.savedUser
        MobileAds.initialize(this, admobAppID)
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


}