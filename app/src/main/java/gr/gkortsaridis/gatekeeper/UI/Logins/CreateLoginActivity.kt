package gr.gkortsaridis.gatekeeper.UI.Logins

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginDeleteListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginRetrieveListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity


class CreateLoginActivity : AppCompatActivity() {

    private val TAG = "_Create_Login_Activity_"

    private lateinit var toolbar: Toolbar

    private lateinit var name: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var notes: EditText
    private lateinit var applicationView: ImageButton
    private lateinit var vaultView: LinearLayout
    private lateinit var saveUpdateButton: Button
    private lateinit var vaultName: TextView
    private lateinit var copyUsername: ImageButton
    private lateinit var copyPassword: ImageButton
    private lateinit var url: EditText

    private var vaultToAdd: Vault? = null
    private var login: Login? = null

    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_login)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        name = findViewById(R.id.nameET)
        username = findViewById(R.id.usernameET)
        password = findViewById(R.id.passwordET)
        url = findViewById(R.id.urlET)
        notes = findViewById(R.id.notesET)
        applicationView = findViewById(R.id.select_application)
        saveUpdateButton = findViewById(R.id.save_update_button)
        vaultView = findViewById(R.id.vault_view)
        vaultName = findViewById(R.id.vault_name)
        copyPassword = findViewById(R.id.copy_password)
        copyUsername = findViewById(R.id.copy_username)

        copyUsername.setOnClickListener { copy(username.text.toString(), "Username") }
        copyPassword.setOnClickListener { copy(password.text.toString(), "Password") }

        vaultView.setOnClickListener {
            val intent = Intent(this, SelectVaultActivity::class.java)
            intent.putExtra("action", "change_login_vault")
            intent.putExtra("vault_id",vaultToAdd?.id)
            startActivityForResult(intent, 14)
        }
        applicationView.setOnClickListener { startActivityForResult(Intent(this, ApplicationSelector::class.java), 13) }
        this.activity = this
        updateUI()
    }

    private fun copy(txt: String, what: String) {
        val clipboard = ContextCompat.getSystemService(
            this,
            ClipboardManager::class.java
        ) as ClipboardManager
        val clip = ClipData.newPlainText("label",txt)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "$what copied", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        val loginId = intent.getStringExtra("login_id")

        if (loginId == null) {
            supportActionBar?.title = "Create new login"
            vaultToAdd = VaultRepository.getLastActiveVault()
            saveUpdateButton.setOnClickListener { createLogin() }
        }else{
            login = LoginsRepository.getLoginById(loginId)
            supportActionBar?.title = "Edit Login"

            vaultToAdd = VaultRepository.getVaultByID(login!!.vault_id)!!

            name.setText(login?.name)
            username.setText(login?.username)
            password.setText(login?.password)
            notes.setText(login?.notes)
            vaultName.text = vaultToAdd?.name
            url.setText(login?.url)
            saveUpdateButton.setOnClickListener { updateLogin() }
        }
    }

    private fun updateLogin() {
        login?.username = username.text.toString()
        login?.name = name.text.toString()
        login?.password = password.text.toString()
        login?.notes = notes.text.toString()
        login?.url = url.text.toString()
        login?.vault_id = vaultToAdd!!.id

        LoginsRepository.encryptAndUpdateLogin(this, login!!, object : LoginCreateListener{
            override fun onLoginCreated() {
                val viewDialog = ViewDialog(activity)
                viewDialog.showDialog()

                //TODO: Remove this call
                LoginsRepository.retrieveLoginsByAccountID(AuthRepository.getUserID(), object:
                    LoginRetrieveListener {
                    override fun onLoginsRetrieveSuccess(logins: ArrayList<Login>) {
                        viewDialog.hideDialog()
                        GateKeeperApplication.logins = logins
                        val data = Intent()
                        setResult(LoginsRepository.createLoginSuccess, data)

                        finish()
                    }

                    override fun onLoginsRetrieveError(e: Exception) {
                        viewDialog.hideDialog()
                        val data = Intent()
                        setResult(LoginsRepository.createLoginError, data)
                        finish()
                    }
                })



            }

            override fun onLoginCreateError() {
                val data = Intent()
                setResult(LoginsRepository.createLoginError, data)
                finish()
            }
        })
    }

    private fun createLogin() {
        val loginObj = Login(account_id = AuthRepository.getUserID(),
            vault_id = vaultToAdd!!.id,
            name = name.text.toString(),
            password = password.text.toString(),
            username = username.text.toString(),
            url = url.text.toString(),
            notes = notes.text.toString(),
            date_created = Timestamp.now(),
            date_modified = Timestamp.now()
        )

        LoginsRepository.encryptAndStoreLogin(this, loginObj, object : LoginCreateListener{
                override fun onLoginCreated() {
                    val viewDialog = ViewDialog(activity)
                    viewDialog.showDialog()

                    LoginsRepository.retrieveLoginsByAccountID(AuthRepository.getUserID(), object:
                        LoginRetrieveListener {
                        override fun onLoginsRetrieveSuccess(logins: ArrayList<Login>) {
                            viewDialog.hideDialog()
                            GateKeeperApplication.logins = logins
                            val data = Intent()
                            setResult(LoginsRepository.createLoginSuccess, data)

                            finish()
                        }

                        override fun onLoginsRetrieveError(e: Exception) {
                            viewDialog.hideDialog()
                            val data = Intent()
                            setResult(LoginsRepository.createLoginError, data)
                            finish()
                        }
                    })



                }

                override fun onLoginCreateError() {
                    val data = Intent()
                    setResult(LoginsRepository.createLoginError, data)
                    finish()
                }
            })

    }

    private fun deleteLogin() {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()

        LoginsRepository.deleteLogin(login!!, object: LoginDeleteListener {
            override fun onLoginDeleted() {
                LoginsRepository.retrieveLoginsByAccountID(AuthRepository.getUserID(), object:
                    LoginRetrieveListener {
                    override fun onLoginsRetrieveSuccess(logins: ArrayList<Login>) {
                        viewDialog.hideDialog()
                        GateKeeperApplication.logins = logins
                        val data = Intent()
                        setResult(LoginsRepository.deleteLoginSuccess, data)

                        finish()
                    }

                    override fun onLoginsRetrieveError(e: Exception) {
                        viewDialog.hideDialog()
                        val data = Intent()
                        setResult(LoginsRepository.createLoginError, data)
                        finish()
                    }
                })
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (login != null) {
            menuInflater.inflate(R.menu.delete_item_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) { deleteLogin() }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 13 && resultCode == Activity.RESULT_OK) {
            val app = data!!.getParcelableExtra<ResolveInfo>("app")
            this.url.setText(app?.activityInfo?.packageName)
        }else if (requestCode == 14 && resultCode == Activity.RESULT_OK) {
            val vaultId = data!!.data.toString()
            login?.vault_id = vaultId
            updateUI()
        }

    }
}
