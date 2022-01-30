package gr.gkortsaridis.gatekeeper.UI.Logins

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.Composables.GateKeeperTextField.GateKeeperTextField
import gr.gkortsaridis.gatekeeper.UI.Composables.GateKeeperVaultSelector.vaultSelector
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperDevelopMockData
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperShapes
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme
import gr.gkortsaridis.gatekeeper.Utils.Status
import gr.gkortsaridis.gatekeeper.ViewModels.LoginDetailsViewModel
import gr.gkortsaridis.gatekeeper.UI.Composables.GateKeeperTextField.InputType

@AndroidEntryPoint
class CreateLoginActivity : AppCompatActivity() {

    private val viewModel: LoginDetailsViewModel by viewModels()
    private val viewDialog = ViewDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginId = intent.getStringExtra("login_id")
        val login = viewModel.getLoginById(loginId)
        val currentVault = viewModel.getLastActiveVault()

        setContent { LoginDetailsPage(
            currentVault = currentVault,
            login = login
        ) }

        viewModel.createLoginData.observe(this) {
            when (it.status) {
                Status.LOADING -> { viewDialog.showDialog() }
                Status.ERROR -> {
                    viewDialog.hideDialog()
                    Toast.makeText(this, it.message ?: "", Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    viewDialog.hideDialog()
                    if(it.data != null) {
                        viewModel.insertLocalLogin(it.data)
                        finish()
                    } else {
                        Toast.makeText(this, "We encountered an error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        /*setContentView(R.layout.activity_create_login)

        AnalyticsRepository.trackEvent(AnalyticsRepository.LOGIN_INFO)

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
            login = viewModel.getLoginById(loginId)!!
            activity_title.text = "Edit Password"
            vaultToAdd = VaultRepository.getVaultByID(login.vault_id)!!
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
                        AnalyticsRepository.trackEvent(AnalyticsRepository.LOGIN_PASS_COPY)
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
                        AnalyticsRepository.trackEvent(AnalyticsRepository.LOGIN_USER_COPY)
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
        updateUI()*/
    }


    @Composable
    @Preview
    fun LoginDetailsPage(
        currentVault: Vault = GateKeeperDevelopMockData.mockVault,
        login: Login? = null
    ) {
        var loginToShow = login ?: Login(
            account_id = currentVault.account_id,
            name = "",
            username = "",
            password = "",
            url = "",
            notes = "",
            vault_id = currentVault.id
        )

        var dataNotEmpty by remember { mutableStateOf(dataNotEmpty(loginToShow)) }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(GateKeeperTheme.light_grey)
        ) {
            toolbar(login = loginToShow)
            vaultSelector(currentVault = currentVault)

            inputCard(
                login = loginToShow,
                onLoginChane = {
                    loginToShow = it
                    dataNotEmpty = dataNotEmpty(it)
                }
            )
            Spacer(modifier = Modifier.weight(1f))

            bottomButton(
                dataNotEmpty = dataNotEmpty,
                onSavePressed = { handleSaveButton(loginToShow) }
            )
        }

    }

    @Composable
    fun toolbar(
        login: Login
    ) {
        Card(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(),
            backgroundColor = GateKeeperTheme.colorPrimaryDark,
            shape = RoundedCornerShape(0.dp),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if(login == null) "Create Login" else "Edit Login",
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    color = GateKeeperTheme.white,
                    fontSize = 19.sp
                )

                if(login != null) {
                    Image(
                        painter = painterResource(id = R.drawable.delete_grey),
                        contentDescription = "Delete Login",
                        colorFilter = ColorFilter.tint(GateKeeperTheme.white),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(24.dp, 24.dp)
                            .clickable { handleDeleteButton() },
                    )
                }

            }

        }
    }

    @Composable
    fun inputCard(
        login: Login,
        onLoginChane: (Login) -> Unit = {}
    ) {
        Card(
            shape = GateKeeperShapes.getLeftRadiusCard(250),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp),
            backgroundColor = GateKeeperTheme.white,
            elevation = 4.dp
        ) {
            Column{

                GateKeeperTextField(
                    modifier = Modifier.padding(start = 32.dp, end=16.dp, top=32.dp, bottom = 8.dp),
                    placeholder="Name",
                    value = login.name,
                    onTextChange = { login.name = it; onLoginChane(login) }
                )

                GateKeeperTextField(
                    modifier = Modifier.padding(start = 32.dp, end=16.dp, top=8.dp, bottom = 8.dp),
                    placeholder="Username",
                    value = login.username,
                    onTextChange = {login.username = it; onLoginChane(login) }
                )

                GateKeeperTextField(
                    modifier = Modifier.padding(start = 32.dp, end=16.dp, top=8.dp, bottom = 8.dp),
                    placeholder="Password",
                    value = login.password,
                    inputType = InputType.PASSWORD,
                    onTextChange = {login.password = it; onLoginChane(login) }
                )

                GateKeeperTextField(
                    modifier = Modifier.padding(start = 32.dp, end=16.dp, top=8.dp, bottom = 8.dp),
                    placeholder="URL",
                    value = login.url,
                    onTextChange = {login.url = it; onLoginChane(login) }
                )

                GateKeeperTextField(
                    modifier = Modifier.padding(start = 32.dp, end=16.dp, top=8.dp, bottom = 32.dp),
                    placeholder="Notes",
                    value = login.notes ?: "",
                    inputType = InputType.MULTILINE,
                    onTextChange = {login.notes = it; onLoginChane(login) }
                )
            }
        }

    }

    @Composable
    fun bottomButton(
        dataNotEmpty: Boolean,
        onSavePressed: () -> Unit = {}
    ) {
        Card(
            shape = GateKeeperShapes.getArcButtonShape(diagonalDp = 200),
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clickable { onSavePressed() },
            backgroundColor = if(dataNotEmpty) GateKeeperTheme.colorAccent else GateKeeperTheme.busy_grey,
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Save",
                    color = GateKeeperTheme.white,
                    fontSize = 20.sp
                )
            }
        }
    }

    private fun handleDeleteButton() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Password")
        builder.setMessage("Are you sure you want to delete this Password item?")
        builder.setPositiveButton("DELETE"){dialog, _ ->
            dialog.cancel()
        }
        builder.setNegativeButton("CANCEL"){dialog, _ -> dialog.cancel() }
        val dialog: AlertDialog = builder.create()
        dialog.show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(resources.getColor(R.color.error_red))
    }

    private fun handleSaveButton(login: Login) {
        Log.i("SAVING", "Name: "+login.name)
        Log.i("SAVING", "Username: "+login.username)
        Log.i("SAVING", "Password: "+login.password)
        Log.i("SAVING", "URL: "+login.url)
        Log.i("SAVING", "Notes: "+login.notes)

        if(login.id == "-1") { viewModel.createLogin(login) }
        else {}

    }

    private fun dataNotEmpty(login: Login): Boolean {
        return (login.name.isNotBlank() || login.username.isNotBlank() || login.password.isNotBlank() || login.url.isNotBlank() || (login.notes ?: "").isNotBlank())
    }
    /*

    private fun copy(txt: String, what: String) {
        val clipboard = ContextCompat.getSystemService(
            this,
            ClipboardManager::class.java
        ) as ClipboardManager
        val clip = ClipData.newPlainText("label",txt)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "$what copied", Toast.LENGTH_SHORT).show()
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
                        AnalyticsRepository.trackEvent(AnalyticsRepository.LOGIN_UPDATE)
                        val data = Intent()
                        setResult(LoginsRepository.createLoginSuccess, data)
                        finish()
                    }
                }

                override fun onLoginUpdateError(errorCode: Int, errorMsg: String) {
                    viewDialog.hideDialog()
                    val data = Intent()
                    setResult(LoginsRepository.createLoginError, data)
                    AnalyticsRepository.trackEvent(AnalyticsRepository.LOGIN_UPDATE_ERROR)
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
                    AnalyticsRepository.trackEvent(AnalyticsRepository.LOGIN_CREATE)
                    val data = Intent()
                    setResult(LoginsRepository.createLoginSuccess, data)
                    finish()
                }

                override fun onLoginCreateError(errorCode: Int, errorMsg: String) {
                    viewDialog.hideDialog()
                    AnalyticsRepository.trackEvent(AnalyticsRepository.LOGIN_CREATE_ERROR)
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
                AnalyticsRepository.trackEvent(AnalyticsRepository.LOGIN_DELETE)

                //LoginsRepository.allLogins.remove(login!!)
                viewDialog.hideDialog()
                val data = Intent()
                setResult(LoginsRepository.deleteLoginSuccess, data)
                finish()
            }

            override fun onLoginDeleteError(errorCode: Int, errorMsg: String) {
                viewDialog.hideDialog()
                AnalyticsRepository.trackEvent(AnalyticsRepository.LOGIN_DELETE_ERROR)

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
     */
}
