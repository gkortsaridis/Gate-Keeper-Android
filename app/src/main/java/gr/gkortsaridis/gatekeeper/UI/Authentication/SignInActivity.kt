package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.lifecycle.ViewModelProvider
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.SignInListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AnalyticsRepository
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.UI.Composables.GateKeeperTextField.GateKeeperTextField
import gr.gkortsaridis.gatekeeper.UI.Composables.GateKeeperTextField.InputType
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme
import gr.gkortsaridis.gatekeeper.ViewModels.SignInViewModel

class SignInActivity : ComponentActivity(), SignInListener {

    private val TAG = "_Login_Activity_"

    private lateinit var signIn: Button
    private lateinit var signUpLink: TextView
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var saveCredentials: CheckBox

    private lateinit var signInViewModel: SignInViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        setContent { SignInPage() }

        /*setContentView(R.layout.activity_sign_in)


        signIn = findViewById(R.id.sign_in)
        signUpLink = findViewById(R.id.sign_up_link)
        email = findViewById(R.id.emailET)
        password = findViewById(R.id.passwordET)
        saveCredentials = findViewById(R.id.save_credentials)

        signIn.setOnClickListener { signIn(emailET.text.toString(), password.text.toString(), false) }
        signUpLink.setOnClickListener { signUp() }

        val loadedCredentials = AuthRepository.loadCredentials()
        if (loadedCredentials != null) {
            saveCredentials.isChecked = true
            email.setText(loadedCredentials.email)
            //password.setText(loadedCredentials.password)
        }else{
            saveCredentials.isChecked = false
        }*/
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
        val checkedState = remember { mutableStateOf(signInViewModel.rememberPassword) }

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
                    onTextChange = { signInViewModel.emailStr = it }
                )
                Divider(thickness = 16.dp, color = Color.Transparent)
                GateKeeperTextField(
                    placeholder = "Password",
                    inputType = InputType.PASSWORD,
                    onTextChange = { signInViewModel.passwordStr = it }
                )

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checkedState.value,
                        onCheckedChange = {
                            signInViewModel.rememberPassword = it
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
                    onClick = {
                        Toast.makeText(GateKeeperApplication.instance.applicationContext, signInViewModel.emailStr+" "+signInViewModel.passwordStr, Toast.LENGTH_SHORT).show()
                    }) {
                    Text("SIGN IN")
                }


                Row(modifier = Modifier.padding(top = 24.dp)) {
                    Text("New User? ")
                    Text(
                        text = "Sign Up",
                        color = GateKeeperTheme.colorAccent,
                        fontWeight = FontWeight.Bold
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

    private fun signIn(email: String, password: String, check: Boolean) {
        AuthRepository.signIn(this, email, password, this)
    }

    override fun onSignInComplete(userId: String) {
        val biometricManager = BiometricManager.from(this)
        AnalyticsRepository.trackEvent(AnalyticsRepository.SIGN_IN_PASS)

        if (AuthRepository.getPreferredAuthType() == AuthRepository.SIGN_IN_NOT_SET
            && biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            AlertDialog.Builder(this)
                .setTitle("Biometric Sign In")
                .setMessage("Would you like to use our biometric authentication feature?\nYour credentials are going to be safely stored on the device.")
                .setPositiveButton("Yes") { _, _ ->
                    DataRepository.preferredAuthType = AuthRepository.BIO_SIN_IN
                    saveCredentials.isChecked = true
                    proceedLogin(user = userId)
                }
                .setNegativeButton("No") { _, _ ->
                    DataRepository.preferredAuthType = AuthRepository.PASSWORD_SIGN_IN
                    proceedLogin(user = userId)
                }
                .show()
        } else {
            proceedLogin(user = userId)
        }
    }

    override fun onSignInError(errorCode: Int, errorMsg: String) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        AnalyticsRepository.trackEvent(AnalyticsRepository.SIGN_IN_ERROR)

    }

    private fun proceedLogin(user: String) {
        //Save credentials locally for decryption
        AuthRepository.saveCredentials(email = email.text.toString(), password = password.text.toString())

        AuthRepository.setApplicationUser(user)
        AuthRepository.proceedLoggedIn(this)
    }
}
