package gr.gkortsaridis.gatekeeper

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import gr.gkortsaridis.gatekeeper.Entities.Folder
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

        lateinit var logins: ArrayList<Login>
        lateinit var vaults: ArrayList<Vault>
        lateinit var folders: ArrayList<Folder>

        lateinit var activeVault : Vault
        lateinit var activeFolder : Folder
    }

}