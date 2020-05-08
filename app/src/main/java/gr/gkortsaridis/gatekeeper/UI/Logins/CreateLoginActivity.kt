package gr.gkortsaridis.gatekeeper.UI.Logins

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import com.github.florent37.shapeofview.shapes.ArcView
import com.google.firebase.Timestamp
import com.maxpilotto.actionedittext.ActionEditText
import com.maxpilotto.actionedittext.actions.Icon
import com.maxpilotto.actionedittext.actions.Toggle
import gr.gkortsaridis.gatekeeper.Database.AppExecutors
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.VaultColor
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginDeleteListener
import gr.gkortsaridis.gatekeeper.Interfaces.LoginUpdateListener
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
import kotlinx.android.synthetic.main.activity_create_login.*
import kotlinx.android.synthetic.main.activity_create_login.vault_icon
import kotlinx.android.synthetic.main.activity_create_login.vault_name
import kotlinx.android.synthetic.main.activity_create_login.vault_view
import kotlinx.android.synthetic.main.fragment_logins.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent


class CreateLoginActivity : AppCompatActivity() {

    private val TAG = "_Create_Login_Activity_"

    private var vaultToAdd: Vault? = null
    private lateinit var login: Login

    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_login)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        delete_login_btn.setOnClickListener { showDeleteLoginDialog() }

        vault_view.setOnClickListener {
            login.name = nameET.text.toString()
            login.password = passwordET.text.toString()
            login.username = usernameET.text.toString()
            login.url = urlET.text.toString()
            login.notes = notesET.text.toString()

            val intent = Intent(this, SelectVaultActivity::class.java)
            intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_VAULT)
            intent.putExtra("vault_id",vaultToAdd?.id)
            startActivityForResult(intent, CHANGE_VAULT_REQUEST_CODE)
        }

        this.activity = this

        val loginId = intent.getStringExtra("login_id")
        if (loginId == null) {
            activity_title.text = "Create new Password"
            vaultToAdd = VaultRepository.getLastActiveRealVault()
            save_update_button.setOnClickListener { createLogin() }
            login = Login(account_id = AuthRepository.getUserID(),
                vault_id = vaultToAdd!!.id,
                name = "",
                password = "",
                username = "",
                url = "",
                notes = "",
                date_created = null,
                date_modified = null
            )

        } else {
            login = LoginsRepository.getLoginById(loginId)!!
            activity_title.text = "Edit Password"
            vaultToAdd = VaultRepository.getVaultByID(login!!.vault_id)!!
            save_update_button.setOnClickListener { updateLogin() }
        }

        KeyboardVisibilityEvent.setEventListener(activity) { isOpen ->
            Tween
                .to(save_arc, Alpha.VIEW, 0.3F)
                .target( if (isOpen) 0.0f else 1.0f)
                .start(ViewTweenManager.get(save_arc))

            save_update_button.visibility = if (isOpen) View.GONE else View.VISIBLE
        }

        passwordET.apply{    // Kotlin
            action(Toggle(context).apply {
                checkedRes = R.drawable.eye
                uncheckedRes = R.drawable.eye_off
                checked = (login.id != "-1")
                setPassword(checked)

                onToggle = { checked ->
                    setPassword(checked)
                }
            })
            if (login.id != "-1") {
                action(Icon(context).apply {
                    icon = R.drawable.copy
                    onClick = {
                        copy(passwordET.text!!, "Password")
                    }
                })
            }

            showActions()   // This displays all the actions that are added
        }

        if (login.id != "-1") {
            usernameET.apply{    // Kotlin
                action(Icon(context).apply {
                    icon = R.drawable.copy
                    onClick = {
                        copy(usernameET.text!!, "Username")
                    }
                })

                showActions()   // This displays all the actions that are added
            }

        }

        urlET.apply{    // Kotlin
            action(Icon(context).apply {
                icon = R.drawable.order
                onClick = {
                    startActivityForResult(Intent(context, ApplicationSelector::class.java), CHANGE_APP_REQUEST_CODE)
                }
            })

            showActions()   // This displays all the actions that are added
        }


        nameET.addTextChangedListener { toggleSaveButton() }
        getEditTextFromView(usernameET).addTextChangedListener { toggleSaveButton() }
        getEditTextFromView(passwordET).addTextChangedListener { toggleSaveButton() }
        getEditTextFromView(urlET).addTextChangedListener { toggleSaveButton() }
        getEditTextFromView(notesET).addTextChangedListener { toggleSaveButton() }


        toggleSaveButton()
        updateUI()
    }

    private fun dataNotEmpty(): Boolean {
        return (nameET.text.isNotBlank() || usernameET.text?.isNotBlank() == true || passwordET.text?.isNotBlank() == true || urlET.text?.isNotBlank() == true || notesET.text?.isNotBlank() == true)
    }

    private fun toggleSaveButton() {
        save_update_button.setBackgroundColor(
            if (dataNotEmpty()) {
                resources.getColor(R.color.colorPrimaryDark)
            } else {
                resources.getColor(R.color.greyish)
            }
        )
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
        nameET.setText(login.name)
        usernameET.apply { text = login.username }
        passwordET.apply { text = login.password }
        urlET.apply { text = login.url }
        notesET.apply { text = login.notes }
        delete_login_btn.visibility = if (login.id != "-1") View.VISIBLE else View.GONE

        vault_name.text = vaultToAdd?.name
        vault_view.setBackgroundColor(resources.getColor(vaultToAdd?.getVaultColorResource() ?: R.color.colorPrimaryDark))
        vault_name.setTextColor(resources.getColor(vaultToAdd?.getVaultColorAccent() ?: R.color.colorPrimaryDark))
        vault_icon.setColorFilter(resources.getColor(vaultToAdd?.getVaultColorAccent() ?: R.color.colorPrimaryDark))
    }

    private fun updateLogin() {
        if (dataNotEmpty()) {
            login.username = usernameET.text.toString()
            login.name = nameET.text.toString()
            login.password = passwordET.text.toString()
            login.notes = notesET.text.toString()
            login.url = urlET.text.toString()
            login.vault_id = vaultToAdd!!.id
            login.date_modified = null

            val viewDialog = ViewDialog(activity)
            viewDialog.showDialog()

            LoginsRepository.encryptAndUpdateLogin(this, login, object : LoginUpdateListener{
                override fun onLoginUpdated(login: Login) {
                    AppExecutors.instance.diskIO.execute {
                        LoginsRepository.updateLocalLogin(login)
                        viewDialog.hideDialog()
                        val data = Intent()
                        setResult(LoginsRepository.createLoginSuccess, data)
                        finish()
                    }
                }

                override fun onLoginUpdateError(errorCode: Int, errorMsg: String) {
                    viewDialog.hideDialog()
                    val data = Intent()
                    setResult(LoginsRepository.createLoginError, data)
                    finish()
                }
            })
        } else {
            Toast.makeText(this, "Cannot save empty password item. If you do not want this item anymore, please delete it", Toast.LENGTH_SHORT).show()
        }

    }

    private fun createLogin() {
        if (dataNotEmpty()) {
            val viewDialog = ViewDialog(activity)
            viewDialog.showDialog()

            val loginObj = Login(account_id = AuthRepository.getUserID(),
                vault_id = vaultToAdd!!.id,
                name = nameET.text.toString(),
                password = passwordET.text.toString(),
                username = usernameET.text.toString(),
                url = urlET.text.toString(),
                notes = notesET.text.toString(),
                date_created = null,
                date_modified = null
            )

            LoginsRepository.encryptAndStoreLogin(this, loginObj, object : LoginCreateListener{
                override fun onLoginCreated(login: Login) {
                    viewDialog.hideDialog()
                    LoginsRepository.addLocalLogin(login)
                    //LoginsRepository.allLogins.add(login)
                    val data = Intent()
                    setResult(LoginsRepository.createLoginSuccess, data)
                    finish()
                }

                override fun onLoginCreateError(errorCode: Int, errorMsg: String) {
                    viewDialog.hideDialog()
                    Toast.makeText(baseContext, errorMsg, Toast.LENGTH_SHORT).show()
                    val data = Intent()
                    setResult(LoginsRepository.createLoginError, data)
                    finish()
                }
            })
        } else {
            Toast.makeText(this, "Cannot save empty password item", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteLogin() {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()

        LoginsRepository.deleteLogin(login!!, object: LoginDeleteListener {
            override fun onLoginDeleted() {
                LoginsRepository.removeLocalLogin(login)
                //LoginsRepository.allLogins.remove(login!!)
                viewDialog.hideDialog()
                val data = Intent()
                setResult(LoginsRepository.deleteLoginSuccess, data)
                finish()
            }

            override fun onLoginDeleteError(errorCode: Int, errorMsg: String) {
                viewDialog.hideDialog()
                val data = Intent()
                setResult(LoginsRepository.deleteLoginError, data)
                Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun showDeleteLoginDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Password")
        builder.setMessage("Are you sure you want to delete this Password item?")
        builder.setPositiveButton("DELETE"){dialog, _ ->
            dialog.cancel()
            deleteLogin()
        }
        builder.setNegativeButton("CANCEL"){dialog, _ -> dialog.cancel() }
        val dialog: AlertDialog = builder.create()
        dialog.show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(resources.getColor(R.color.error_red))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHANGE_APP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val app = data!!.getParcelableExtra<ResolveInfo>("app")
            urlET.apply { text = app?.activityInfo?.packageName }
        }else if (requestCode == CHANGE_VAULT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val vaultId = data!!.data.toString()
            vaultToAdd = VaultRepository.getVaultByID(vaultId)
            updateUI()
        }

    }

    private fun getEditTextFromView(view: ActionEditText): AppCompatEditText {
        //Get the EditText View from the custom View
        val linearLayout = view[0] as LinearLayout
        val relativeLayout = linearLayout[1] as RelativeLayout
        return relativeLayout[0] as AppCompatEditText
    }
}
