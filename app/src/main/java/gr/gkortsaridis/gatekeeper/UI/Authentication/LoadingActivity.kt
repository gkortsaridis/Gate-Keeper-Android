package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import gr.gkortsaridis.gatekeeper.Entities.Folder
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.FolderRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultRetrieveListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.FolderRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.MainActivity
import kotlin.Exception

class LoadingActivity : AppCompatActivity(), LoginRetrieveListener, VaultRetrieveListener, FolderRetrieveListener {

    private val TAG = "_Loading_Activity_"

    private var loginsOk : Boolean = false
    private var vaultsOk : Boolean = false
    private var foldersOk : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        LoginsRepository.retrieveLoginsByAccountID(GateKeeperApplication.user.uid, this)
        VaultRepository.retrieveVaultsByAccountID(GateKeeperApplication.user.uid, this)
        FolderRepository.retrieveFoldersByAccountID(GateKeeperApplication.user.uid, this)
    }

    private fun openMainApplication() {
        if (loginsOk && vaultsOk && foldersOk) {
            if (GateKeeperApplication.folders.size > 0){
                GateKeeperApplication.activeFolder = GateKeeperApplication.folders[0]
            }

            if (GateKeeperApplication.vaults.size > 0) {
                GateKeeperApplication.activeVault = GateKeeperApplication.vaults[0]
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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

    override fun onFoldersRetrieveSuccess(folders: ArrayList<Folder>) {
        for (folder in folders) { Log.i(TAG, folder.toString()) }

        GateKeeperApplication.folders = folders
        foldersOk = true
        openMainApplication()
    }

    //Error Retrieving cases :(
    override fun onFoldersRetrieveError(e: Exception) {
        e.printStackTrace()
        //TODO: Update UI
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
