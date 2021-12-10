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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import gr.gkortsaridis.gatekeeper.Interfaces.SignInListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AnalyticsRepository
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.ViewModels.SignInViewModel
import kotlinx.android.synthetic.main.activity_sign_in.*


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

        setContent {
            SignInPage()
        }

        /*setContentView(R.layout.activity_sign_in)

        signInViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)

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
                .background(Color(0xFFFFFACD))
        ) {
            topPart()
            inputCard()
        }

    }

    @Composable
    fun inputCard() {
        val usernameState = remember { mutableStateOf(TextFieldValue()) }
        val passwordState = remember { mutableStateOf(TextFieldValue()) }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 200.dp)
                .background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier

                    .width(300.dp)
                    .background(Color.Red),
                elevation = 5.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Hello")
                    TextField(
                        value = usernameState.value,
                        label = { Text("Email") },
                        onValueChange = { usernameState.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.background(Color.Transparent),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Red
                        ),
                        leadingIcon = { Icon(painterResource(id = R.drawable.access), contentDescription = "", modifier = Modifier.size(20.dp)) }
                    )
                    TextField(
                        value = passwordState.value,
                        onValueChange = { passwordState.value = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent
                        )
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
                .background(Color.Blue),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.padlock),
                contentDescription = "Localized description",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp, 70.dp)
                    .background(Color.Red)
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
