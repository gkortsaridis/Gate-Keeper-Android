package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import gr.gkortsaridis.gatekeeper.Entities.UserCredentials
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AnalyticsRepository
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme
import gr.gkortsaridis.gatekeeper.Utils.Status
import gr.gkortsaridis.gatekeeper.ViewModels.SignInViewModel

@AndroidEntryPoint
class BioAuthenticationActivity : AppCompatActivity() {
    private val viewModel: SignInViewModel by viewModels()
    private val viewDialog = ViewDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SignInPage() }

        viewModel.signInData.observe(this, {
            when(it.status) {
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
                    viewModel.setApplicationUser(userId)
                    viewModel.proceedLoggedIn(this)
                    AnalyticsRepository.trackEvent(AnalyticsRepository.SIGN_IN_BIO)
                }
            }
        })

        startBioAuth()
    }

    @Preview
    @Composable
    fun SignInPage() {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(GateKeeperTheme.colorPrimaryDark),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(150.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Localized description",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp, 100.dp)
            )


            Image(
                painter = painterResource(id = R.drawable.access),
                contentDescription = "Localized description",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp, 150.dp)
                    .clickable { startBioAuth() }
            )

            Spacer(modifier = Modifier.weight(1.0f))

            Text(
                color = GateKeeperTheme.white,
                text = "Click the fingerprint icon to try again",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.size(16.dp))

        }

    }

    private fun startBioAuth() {
        val loadedCredentials = viewModel.loadCredentials()
        if (loadedCredentials != null) {
            showBioAuth(loadedCredentials)
        } else {
            Toast.makeText(applicationContext, "Could not load user credentials. Please log in using password to enable biometric authentication", Toast.LENGTH_LONG).show()
            goToPassword()
        }
    }

    private fun showBioAuth(credentials: UserCredentials) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) { goToPassword() }
                    else { Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show() }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    viewModel.email = credentials.email
                    viewModel.password = credentials.password
                    viewModel.signIn()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Gate Keeper")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun goToPassword() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

}
