package gr.gkortsaridis.gatekeeper

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class GateKeeperApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: GateKeeperApplication private set
        lateinit var userAccount: GoogleSignInAccount
    }

}