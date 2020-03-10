package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.biometric.BiometricManager
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository

class AuthenticationBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication_base)

        val preferredAuthType = AuthRepository.getPreferredAuthType()
        val canAuthenticateWithBio = BiometricManager.from(this).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS

        val intent = if (preferredAuthType == AuthRepository.BIO_SIN_IN && canAuthenticateWithBio) {
            Intent(this, BioAuthenticationActivity::class.java)
        } else if (preferredAuthType == AuthRepository.PIN_SIGN_IN && !DataRepository.pinLock.isNullOrEmpty()) {
            Intent(this, PinAuthenticationActivity::class.java)
        } else {
            Intent(this, SignInActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        overridePendingTransition(0, 0)

    }
}
