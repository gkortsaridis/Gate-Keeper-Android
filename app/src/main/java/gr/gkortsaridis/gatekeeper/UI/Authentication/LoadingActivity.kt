package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.DevicesRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultRetrieveListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.*
import gr.gkortsaridis.gatekeeper.UI.MainActivity


class LoadingActivity : AppCompatActivity(), LoginRetrieveListener, VaultRetrieveListener, CreditCardRetrieveListener, DevicesRetrieveListener {

    private val TAG = "_Loading_Activity_"

    private var loginsOk : Boolean = false
    private var vaultsOk : Boolean = false
    private var devicesOk: Boolean = false
    private var cardsOk  : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        LoginsRepository.retrieveLoginsByAccountID(AuthRepository.getUserID(), this)
        VaultRepository.retrieveVaultsByAccountID(AuthRepository.getUserID(), this)
        CreditCardRepository.retrieveCardsByAccountID(AuthRepository.getUserID(), this)
        DeviceRepository.retrieveDevicesByAccountID(AuthRepository.getUserID(), this)
    }

    private fun openMainApplication() {
        if (loginsOk && vaultsOk && devicesOk && cardsOk) {
            DeviceRepository.logCurrentLogin(this)

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onLoginsRetrieveSuccess(logins: ArrayList<Login>) {
        GateKeeperApplication.logins = logins
        loginsOk = true
        openMainApplication()
    }

    override fun onVaultsRetrieveSuccess(vaults: ArrayList<Vault>) {
        GateKeeperApplication.vaults = vaults
        vaultsOk = true
        openMainApplication()
    }

    override fun onCreditCardsReceived(cards: ArrayList<CreditCard>) {
        GateKeeperApplication.cards = cards
        cardsOk = true
        openMainApplication()
    }

    override fun onDevicesRetrieved(devices: ArrayList<Device>) {
        GateKeeperApplication.devices = devices
        devicesOk = true
        openMainApplication()
    }

    override fun onLoginsRetrieveError(e: Exception) {
        e.printStackTrace()
        showLoadingError()
    }

    override fun onVaultsRetrieveError(e: Exception) {
        e.printStackTrace()
        showLoadingError()
    }

    override fun onCreditCardsReceiveError(e: java.lang.Exception) {
        e.printStackTrace()
        showLoadingError()
    }

    override fun onDeviceRetrieveError(exception: java.lang.Exception) {
        exception.printStackTrace()
        showLoadingError()
    }

    fun showLoadingError() {
        Toast.makeText(this, "We encountered an error loading your data. Please try again", Toast.LENGTH_SHORT).show()
        finish()
    }

}
