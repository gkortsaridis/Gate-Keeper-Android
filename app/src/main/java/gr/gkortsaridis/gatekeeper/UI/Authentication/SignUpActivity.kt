package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.SignUpListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultSetupListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository

class SignUpActivity : AppCompatActivity() {

    private val TAG = "_Sign_Up_"

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var signUp: Button
    private lateinit var repeatPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        email = findViewById(R.id.emailET)
        password = findViewById(R.id.passwordET)
        signUp = findViewById(R.id.sign_up)
        repeatPassword = findViewById(R.id.repeatPasswordET)

        signUp.setOnClickListener {
            var error = false
            if (password.text.toString() != repeatPassword.text.toString()) {
                error = true
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else if (!isValidEmail(email.text.toString())) {
                error = true
                Toast.makeText(this, "This is not a valid email", Toast.LENGTH_SHORT).show()
            } else if (email.text.toString().isEmpty() || password.text.toString().isEmpty() || repeatPassword.text.toString().isEmpty()) {
                error = true
                Toast.makeText(this, "Fields must not be empty", Toast.LENGTH_SHORT).show()
            }

            if (!error) { signUp(email.text.toString(), password.text.toString()) }
        }
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun signUp(email: String, password: String) {
        AuthRepository.signUp(this, email,password, object : SignUpListener{
            override fun onSignUpComplete(user: String) {
                AuthRepository.saveCredentials(email = email, password = password)
                AuthRepository.setApplicationUser(user)
                VaultRepository.setupVaultsForNewUser(user, object: VaultSetupListener {
                    override fun onVaultSetupComplete() { finalizeSetup() }
                    override fun onVaultSetupError(errorCode: Int, errorMsg: String) { showSetupError(errorMsg) }
                })
            }
            override fun onSignUpError(errorCode: Int, errorMsg: String) { showSetupError(errorMsg) }
        })
    }

    fun finalizeSetup() { AuthRepository.proceedLoggedIn(this) }

    fun showSetupError(errorMsg: String) {
        Toast.makeText(baseContext, errorMsg, Toast.LENGTH_SHORT).show()
    }


}
