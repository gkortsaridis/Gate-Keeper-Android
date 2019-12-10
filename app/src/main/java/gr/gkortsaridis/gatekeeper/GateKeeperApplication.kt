package gr.gkortsaridis.gatekeeper

import android.app.Application
import com.google.firebase.auth.FirebaseUser
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository

class GateKeeperApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        user_id = DataRepository.savedUser
    }

    companion object {
        lateinit var instance: GateKeeperApplication private set
        var user: FirebaseUser? = null
        var user_id: String? = null

        lateinit var logins: ArrayList<Login>
        lateinit var vaults: ArrayList<Vault>
        lateinit var cards: ArrayList<CreditCard>
        var devices: ArrayList<Device>? = null

        lateinit var activeVault : Vault
    }


}