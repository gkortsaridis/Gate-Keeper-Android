package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import gr.gkortsaridis.gatekeeper.Entities.UserCredentials
import gr.gkortsaridis.gatekeeper.Interfaces.SignInListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AnalyticsRepository
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import org.json.JSONObject

class BioAuthenticationActivity : AppCompatActivity() {

    private val activity = this
    private lateinit var startBio : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bio_authentication)

        startBio = findViewById(R.id.start_bio)
        startBio.setOnClickListener { startBioAuth() }

        startBioAuth()
    }

    private fun startBioAuth() {
        val loadedCredentials = AuthRepository.loadCredentials()
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
                    AuthRepository.signIn(activity, credentials.email, credentials.password, object: SignInListener{
                        override fun onSignInComplete(userId: String) {
                            AuthRepository.setApplicationUser(userId)
                            AuthRepository.proceedLoggedIn(activity)
                            val props = JSONObject()
                            props.put(AnalyticsRepository.SIGN_IN_TYPE, AnalyticsRepository.SIGN_IN_BIO)
                            AnalyticsRepository.trackEvent(AnalyticsRepository.SIGN_IN, props)
                        }

                        override fun onSignInError(errorCode: Int, errorMsg: String) {
                            Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
                            AnalyticsRepository.trackEvent(AnalyticsRepository.SIGN_IN_ERROR)
                        }
                    })
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
