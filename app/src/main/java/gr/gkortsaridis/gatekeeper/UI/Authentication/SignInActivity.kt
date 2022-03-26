package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AnalyticsRepository
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.UI.Composables.GateKeeperTextField
import gr.gkortsaridis.gatekeeper.UI.Composables.InputType
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme
import gr.gkortsaridis.gatekeeper.Utils.Status
import gr.gkortsaridis.gatekeeper.ViewModels.SignInViewModel

@AndroidEntryPoint
class SignInActivity : ComponentActivity() {
    private val viewModel: SignInViewModel by viewModels()
    private val viewDialog = ViewDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loadedCredentials = SignInViewModel.loadCredentials()
        viewModel.rememberEmail = loadedCredentials != null
        viewModel.email = loadedCredentials?.email ?: ""
        if (loadedCredentials != null) { viewModel.password = loadedCredentials.email }

        setContent { SignInPage() }

        viewModel.signInData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    viewDialog.showDialog()
                }
                Status.ERROR -> {
                    viewDialog.hideDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    AnalyticsRepository.trackEvent(AnalyticsRepository.SIGN_IN_ERROR)
                }
                Status.SUCCESS -> {
                    viewDialog.hideDialog()

                    val userId = it.data!!.userId
                    val biometricManager = BiometricManager.from(this)
                    AnalyticsRepository.trackEvent(AnalyticsRepository.SIGN_IN_PASS)

                    if (SignInViewModel.getPreferredAuthType() == SignInViewModel.SIGN_IN_NOT_SET
                        && biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
                    ) {
                        AlertDialog.Builder(this)
                            .setTitle("Biometric Sign In")
                            .setMessage("Would you like to use our biometric authentication feature?\nYour credentials are going to be safely stored on the device.")
                            .setPositiveButton("Yes") { _, _ ->
                                DataRepository.preferredAuthType = SignInViewModel.BIO_SIN_IN
                                proceedLogin(user = userId)
                            }
                            .setNegativeButton("No") { _, _ ->
                                DataRepository.preferredAuthType = SignInViewModel.PASSWORD_SIGN_IN
                                proceedLogin(user = userId)
                            }
                            .show()
                    } else {
                        proceedLogin(user = userId)
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun SignInPage() {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            topPart()
            bottomPart()
        }

    }

    @Composable
    fun bottomPart() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 200.dp)
                .background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            inputCard()
        }
    }

    @Composable
    fun inputCard() {
        val checkedState = remember { mutableStateOf(viewModel.rememberEmail) }

        Card(
            modifier = Modifier
                .width(300.dp)
                .background(Color.Transparent),
            elevation = 5.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(
                    text = "Sign In",
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 32.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    color = GateKeeperTheme.colorPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                GateKeeperTextField(
                    placeholder = "Username",
                    inputType = InputType.EMAIL,
                    value = viewModel.email,
                    onTextChange = { viewModel.email = it }
                )
                Divider(thickness = 16.dp, color = Color.Transparent)
                GateKeeperTextField(
                    placeholder = "Password",
                    inputType = InputType.PASSWORD,
                    onTextChange = { viewModel.password = it }
                )

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checkedState.value,
                        onCheckedChange = {
                            viewModel.rememberEmail = it
                            checkedState.value = it
                        },
                        colors = CheckboxDefaults.colors(GateKeeperTheme.colorAccent)
                    )
                    Text(
                        text = stringResource(id = R.string.save_username),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Button(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                    ,
                    onClick = { viewModel.signIn()
                    }) {
                    Text("SIGN IN")
                }


                Row(modifier = Modifier.padding(top = 24.dp)) {
                    Text("New User? ")
                    Text(
                        text = "Sign Up",
                        color = GateKeeperTheme.colorAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            signUp()
                        }
                    )
                }


            }

        }
    }


    @Composable
    fun topPart() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(GateKeeperTheme.colorPrimaryDark),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Localized description",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp, 70.dp)
            )
        }
    }


    private fun signUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun proceedLogin(user: String) {
        //Save credentials locally for decryption
        SignInViewModel.saveCredentials(email = viewModel.email, password = viewModel.password)
        SignInViewModel.setApplicationUser(user)
        SignInViewModel.proceedLoggedIn(this)
    }
}
