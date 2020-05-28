package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import gr.gkortsaridis.gatekeeper.Database.MainViewModel
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.*
import gr.gkortsaridis.gatekeeper.UI.MainActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_loading.*


class LoadingActivity : AppCompatActivity() {

    private val TAG = "_Loading_Activity_"

    private var dataOk  : Boolean = false
    private var timerOk : Boolean = false
    private val timerDelaySeconds = 5

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
                        val devicesEncrypted = it.data.devices

                        val vaults = ArrayList<Vault>()
                        val logins = ArrayList<Login>()
                        val cards = ArrayList<CreditCard>()
                        val notes = ArrayList<Note>()
                        val devices = ArrayList<Device>()

                        for (vault in vaultsEncrypted) {
                            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(vault, Vault::class.java) as Vault?
                            if (decrypted != null) {
                                decrypted.id = vault.id.toString()
                                decrypted.dateCreated = vault.dateCreated
                                decrypted.dateModified = vault.dateModified
                                vaults.add(decrypted)
                            }
                        }

                        for (login in loginsEncrypted) {
                            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(login, Login::class.java) as Login?
                            if (decrypted != null) {
                                decrypted.id = login.id.toString()
                                decrypted.date_created = login.dateCreated
                                decrypted.date_modified = login.dateModified
                                logins.add(decrypted)
                            }
                        }

                        for (card in cardsEncrypted) {
                            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(card, CreditCard::class.java) as CreditCard?
                            if (decrypted != null) {
                                decrypted.id = card.id.toString()
                                decrypted.modifiedDate = card.dateModified
                                decrypted.createdDate = card.dateCreated
                                cards.add(decrypted)
                            }
                        }

                        for (note in notesEncrypted) {
                            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(note, Note::class.java) as Note?
                            if (decrypted != null) {
                                decrypted.id = note.id.toString()
                                decrypted.createDate = note.dateCreated
                                decrypted.modifiedDate = note.dateModified
                                notes.add(decrypted)
                            }
                        }

                        for (device in devicesEncrypted) {
                            val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(device, Device::class.java) as Device?
                            if (decrypted != null) {
                                decrypted.id = device.id.toString()
                                devices.add(decrypted)
                            }
                        }

                        VaultRepository.allVaults = vaults
                        LoginsRepository.allLogins = logins
                        CreditCardRepository.allCards = cards
                        NotesRepository.allNotes = notes
                        GateKeeperApplication.devices = devices
                        dataOk = true
                        openMainApplication()
                    } else {
                        showLoadingError()
                    }
                },
                {
                    showLoadingError()
                }
            )

        //val name = GateKeeperApplication.user?.displayName ?: ""
        //welcome_message.text = "Welcome back\n$name"

        Handler().postDelayed({
            timerOk = true
            openMainApplication()
        }, (timerDelaySeconds * 1000).toLong())
    }

    private fun openMainApplication() {
        if (timerOk && dataOk) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun showLoadingError() {
        Toast.makeText(this, "We encountered an error loading your data. Please try again", Toast.LENGTH_SHORT).show()
        finish()
    }

}
