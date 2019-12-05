package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultRetrieveListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.MainActivity


class LoadingActivity : AppCompatActivity(), LoginRetrieveListener, VaultRetrieveListener {

    private val TAG = "_Loading_Activity_"

    private var loginsOk : Boolean = false
    private var vaultsOk : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        LoginsRepository.retrieveLoginsByAccountID(AuthRepository.getUserID(), this)
        VaultRepository.retrieveVaultsByAccountID(AuthRepository.getUserID(), this)
        //FolderRepository.retrieveFoldersByAccountID(GateKeeperApplication.user.uid, this)
    }

    private fun openMainApplication() {
        if (loginsOk && vaultsOk) {

            if (GateKeeperApplication.vaults.size > 0) {
                GateKeeperApplication.activeVault = GateKeeperApplication.vaults[0]
            }

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }


    //Sucess Retrieving cases :)
    override fun onLoginsRetrieveSuccess(logins: ArrayList<Login>) {
        for (login in logins) { Log.i(TAG, login.toString()) }

        GateKeeperApplication.logins = logins
        loginsOk = true
        openMainApplication()
    }

    override fun onVaultsRetrieveSuccess(vaults: ArrayList<Vault>) {
        for (vault in vaults) { Log.i(TAG, vault.toString()) }

        GateKeeperApplication.vaults = vaults
        vaultsOk = true
        openMainApplication()
    }

    override fun onLoginsRetrieveError(e: Exception) {
        e.printStackTrace()
        //TODO: Update UI
    }

    override fun onVaultsRetrieveError(e: Exception) {
        e.printStackTrace()
        //TODO: Update UI
    }

}
