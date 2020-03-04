package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import com.google.firebase.analytics.FirebaseAnalytics
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.*
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.*
import gr.gkortsaridis.gatekeeper.UI.MainActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class LoadingActivity : AppCompatActivity(), LoginRetrieveListener, VaultRetrieveListener, CreditCardRetrieveListener, DevicesRetrieveListener, NoteRetrieveListener {

    private val TAG = "_Loading_Activity_"

    private var loginsOk : Boolean = false
    private var vaultsOk : Boolean = false
    private var devicesOk: Boolean = false
    private var cardsOk  : Boolean = false
    private var notesOk  : Boolean = false
    private var timerOk : Boolean = false
    private val timerDelaySeconds = 5

    private lateinit var welcomeMessage : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val disposable = GateKeeperAPI.api.getAllData(AuthRepository.getUserID())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    if (it.errorCode == -1 && it.data != null) {
                        val vaultsEncrypted = it.data.vaults
                        val loginsEncrypted = it.data.logins
                        val cardsEncrypted = it.data.cards
                        val notesEncrypted = it.data.notes

                        val vaults = ArrayList<Vault>()
                        val logins = ArrayList<Login>()
                        val cards = ArrayList<CreditCard>()
                        val notes = ArrayList<Note>()

                        for (vault in vaultsEncrypted) {
                            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(vault, Vault::class.java) as Vault?
                            if (decrypted != null) {
                                decrypted.id = vault.id.toString()
                                vaults.add(decrypted)
                            }
                        }

                        for (login in loginsEncrypted) {
                            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(login, Login::class.java) as Login?
                            if (decrypted != null) {
                                decrypted.id = login.id.toString()
                                logins.add(decrypted)
                            }
                        }

                        for (card in cardsEncrypted) {
                            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(card, CreditCard::class.java) as CreditCard?
                            if (decrypted != null) {
                                decrypted.id = card.id.toString()
                                cards.add(decrypted)
                            }
                        }

                        for (note in notesEncrypted) {
                            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(note, Note::class.java) as Note?
                            if (decrypted != null) {
                                decrypted.id = note.id.toString()
                                notes.add(decrypted)
                            }
                        }

                        GateKeeperApplication.vaults = vaults
                        GateKeeperApplication.logins = logins
                        GateKeeperApplication.cards = cards
                        GateKeeperApplication.notes = notes
                        openMainApplication()
                    } else {
                        showLoadingError()
                    }
                },
                { showLoadingError() }
            )


        welcomeMessage = findViewById(R.id.welcome_message)
        //val name = GateKeeperApplication.user?.displayName ?: ""
        welcomeMessage.text = "Welcome back"

        /*Handler().postDelayed({
            timerOk = true
            openMainApplication()
        }, (timerDelaySeconds * 1000).toLong())*/
    }

    private fun openMainApplication() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onLoginsRetrieveSuccess(logins: ArrayList<Login>) {
        GateKeeperApplication.logins = logins
        loginsOk = true
        openMainApplication()
    }

    override fun onVaultsRetrieveSuccess(vaults: ArrayList<Vault>) {
        GateKeeperApplication.vaults = vaults
        if (vaults.size > 0) {
            vaultsOk = true
            openMainApplication()
        }else {
            showLoadingError()
        }
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

    override fun onNotesRetrieved(notes: ArrayList<Note>) {
        GateKeeperApplication.notes = notes
        notesOk = true
        openMainApplication()
    }

    override fun onNotesRetrievedError(e: java.lang.Exception) {
        e.printStackTrace()
        showLoadingError()
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

    private fun showLoadingError() {
        Toast.makeText(this, "We encountered an error loading your data. Please try again", Toast.LENGTH_SHORT).show()
        finish()
    }

}
