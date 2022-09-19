package gr.gkortsaridis.gatekeeper.UI.Logins

import android.content.Intent
import android.os.Bundle
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
import gr.gkortsaridis.gatekeeper.Repositories.AnalyticsRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.UI.Composables.GateKeeperTextField
import gr.gkortsaridis.gatekeeper.UI.Composables.InputType
import gr.gkortsaridis.gatekeeper.UI.Composables.vaultSelector
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.*
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants.CHANGE_VAULT_REQUEST_CODE
import gr.gkortsaridis.gatekeeper.ViewModels.LoginDetailsViewModel

@AndroidEntryPoint
class CreateLoginActivity : AppCompatActivity() {

    private val viewModel: LoginDetailsViewModel by viewModels()
    private val viewDialog = ViewDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginId = intent.getStringExtra("login_id")
        val login = viewModel.getLoginById(loginId)
        val currentVault = viewModel.getLastActiveVault()

        AnalyticsRepository.trackEvent(AnalyticsRepository.LOGIN_INFO)

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

        viewModel.updateLoginData.observe(this) {
            when (it.status) {
                Status.LOADING -> { viewDialog.showDialog() }
                Status.ERROR -> {
                    viewDialog.hideDialog()
                    Toast.makeText(this, it.message ?: "", Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    viewDialog.hideDialog()
                    if(it.data != null) {
                        viewModel.updateLocalLogin(it.data)
                        finish()
                    } else {
                        Toast.makeText(this, "We encountered an error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.deleteLoginData.observe(this) {
            when (it.status) {
                Status.LOADING -> { viewDialog.showDialog() }
                Status.ERROR -> {
                    viewDialog.hideDialog()
                    Toast.makeText(this, it.message ?: "", Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    viewDialog.hideDialog()
                    AnalyticsRepository.trackEvent(AnalyticsRepository.LOGIN_DELETE)

                    if(it.data != null) {
                        viewModel.deleteLocalLogin(it.data.toString())

                        val data = Intent()
                        setResult(LoginsRepository.deleteLoginSuccess, data)
                        finish()
                    } else {
                        Toast.makeText(this, "We encountered an error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        /*

        KeyboardVisibilityEvent.setEventListener(activity) { isOpen ->
            Tween
                .to(save_arc, Alpha.VIEW, 0.3F)
                .target( if (isOpen) 0.0f else 1.0f)
                .start(ViewTweenManager.get(save_arc))

            save_update_button.visibility = if (isOpen) View.GONE else View.VISIBLE
        }

        urlET.apply{    // Kotlin
            action(Icon(context).apply {
                icon = R.drawable.order
                onClick = {
                    startActivityForResult(Intent(context, ApplicationSelector::class.java), CHANGE_APP_REQUEST_CODE)
                }
            })
        }
*/
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
            vaultSelector(
                currentVault = currentVault,
                onVaultClick = { onVaultClick(currentVault) }
            )

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
                            .clickable { handleDeleteButton(login) },
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
                .height(60.dp)
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

    private fun handleDeleteButton(login: Login) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Password")
        builder.setMessage("Are you sure you want to delete this Password item?")
        builder.setPositiveButton("DELETE"){dialog, _ ->
            viewModel.deleteLogin(login)
            dialog.cancel()
        }
        builder.setNegativeButton("CANCEL"){dialog, _ -> dialog.cancel() }
        val dialog: AlertDialog = builder.create()
        dialog.show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(resources.getColor(R.color.error_red))
    }

    private fun handleSaveButton(login: Login) {
        if(login.id == "-1") { viewModel.createLogin(login) }
        else { viewModel.updateLogin(login) }
    }

    private fun dataNotEmpty(login: Login): Boolean {
        return (login.name.isNotBlank() || login.username.isNotBlank() || login.password.isNotBlank() || login.url.isNotBlank() || (login.notes ?: "").isNotBlank())
    }

    private fun onVaultClick(currentVault: Vault) {
        val intent = Intent(this, SelectVaultActivity::class.java)
        intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_VAULT)
        intent.putExtra("vault_id",currentVault.id)
        startActivityForResult(intent, CHANGE_VAULT_REQUEST_CODE)
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
     */
}
