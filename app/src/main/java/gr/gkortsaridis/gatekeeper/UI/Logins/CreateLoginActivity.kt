package gr.gkortsaridis.gatekeeper.UI.Logins

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.github.florent37.shapeofview.shapes.ArcView
import com.google.firebase.Timestamp
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginDeleteListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants.CHANGE_APP_REQUEST_CODE
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants.CHANGE_VAULT_REQUEST_CODE
import io.noties.tumbleweed.Tween
import io.noties.tumbleweed.android.ViewTweenManager
import io.noties.tumbleweed.android.types.Alpha
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent


class CreateLoginActivity : AppCompatActivity() {

    private val TAG = "_Create_Login_Activity_"

    private lateinit var toolbar: Toolbar

    private lateinit var name: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var notes: EditText
    private lateinit var applicationView: ImageButton
    private lateinit var vaultView: LinearLayout
    private lateinit var saveUpdateButton: RelativeLayout
    private lateinit var vaultName: TextView
    private lateinit var copyUsername: ImageButton
    private lateinit var copyPassword: ImageButton
    private lateinit var deleteLogin: ImageButton
    private lateinit var title: TextView
    private lateinit var url: EditText
    private lateinit var saveUpdateArc: ArcView

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
        deleteLogin = findViewById(R.id.delete_login_btn)
        title = findViewById(R.id.title)
        saveUpdateArc = findViewById(R.id.save_arc)

        copyUsername.setOnClickListener { copy(username.text.toString(), "Username") }
        copyPassword.setOnClickListener { copy(password.text.toString(), "Password") }
        deleteLogin.setOnClickListener { deleteLogin() }

        vaultView.setOnClickListener {
            val intent = Intent(this, SelectVaultActivity::class.java)
            intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_VAULT)
            intent.putExtra("vault_id",vaultToAdd?.id)
            startActivityForResult(intent, CHANGE_VAULT_REQUEST_CODE)
        }
        applicationView.setOnClickListener { startActivityForResult(Intent(this, ApplicationSelector::class.java), CHANGE_APP_REQUEST_CODE) }
        this.activity = this

        val loginId = intent.getStringExtra("login_id")
        if (loginId == null) {
            title.text = "Create new Password"
            vaultToAdd = VaultRepository.getLastActiveRealVault()
            saveUpdateButton.setOnClickListener { createLogin() }
        } else {
            login = LoginsRepository.getLoginById(loginId)
            title.text = "Edit Password"
            vaultToAdd = VaultRepository.getVaultByID(login!!.vault_id)!!
            saveUpdateButton.setOnClickListener { updateLogin() }
        }

        KeyboardVisibilityEvent.setEventListener(activity) { isOpen ->
            Tween
                .to(saveUpdateArc, Alpha.VIEW, 0.3F)
                .target( if (isOpen) 0.0f else 1.0f)
                .start(ViewTweenManager.get(saveUpdateArc))
        }

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
        name.setText(login?.name)
        username.setText(login?.username)
        password.setText(login?.password)
        notes.setText(login?.notes)
        url.setText(login?.url)
        vaultName.text = vaultToAdd?.name
        deleteLogin.visibility = if (login != null) View.VISIBLE else View.GONE
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
                GateKeeperApplication.logins.replaceAll { if (it.id == login?.id) login!! else it }
                viewDialog.hideDialog()
                val data = Intent()
                setResult(LoginsRepository.createLoginSuccess, data)
                finish()
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
                    GateKeeperApplication.logins.add(loginObj)
                    val data = Intent()
                    setResult(LoginsRepository.createLoginSuccess, data)
                    finish()
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
                GateKeeperApplication.logins.remove(login!!)
                viewDialog.hideDialog()
                val data = Intent()
                setResult(LoginsRepository.deleteLoginSuccess, data)
                finish()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHANGE_APP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val app = data!!.getParcelableExtra<ResolveInfo>("app")
            this.url.setText(app?.activityInfo?.packageName)
        }else if (requestCode == CHANGE_VAULT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val vaultId = data!!.data.toString()
            vaultToAdd = VaultRepository.getVaultByID(vaultId)
            updateUI()
        }

    }
}
