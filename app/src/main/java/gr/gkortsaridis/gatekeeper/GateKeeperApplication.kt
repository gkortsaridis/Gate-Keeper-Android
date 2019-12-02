package gr.gkortsaridis.gatekeeper

import android.app.Application
import com.google.firebase.auth.FirebaseUser
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault

class GateKeeperApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: GateKeeperApplication private set
        lateinit var user: FirebaseUser

        var debugMode: Boolean = true

        lateinit var logins: ArrayList<Login>
        lateinit var vaults: ArrayList<Vault>

        lateinit var activeVault : Vault
    }


}