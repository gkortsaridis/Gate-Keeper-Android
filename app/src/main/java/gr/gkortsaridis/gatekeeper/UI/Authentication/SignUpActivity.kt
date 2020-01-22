package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import gr.gkortsaridis.gatekeeper.Entities.FirebaseSignInResult
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.SignUpListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultSetupListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository

class SignUpActivity : AppCompatActivity(), SignUpListener {

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
        AuthRepository.signUp(this, email,password, this)
    }

    override fun onSignUpComplete(success: Boolean, user: FirebaseSignInResult) {
        if (success) {
            AuthRepository.setApplicationUser(user.authResult!!.user!!)
            VaultRepository.setupVaultsForNewUser(GateKeeperApplication.user!!, object: VaultSetupListener {
                override fun onVaultSetupComplete() {
                    finalizeSetup()
                }
                override fun onVaultSetupError() { showSetupError() }
            })
        }else{
            when (user.exception) {
                is com.google.firebase.auth.FirebaseAuthUserCollisionException -> {
                    Toast.makeText(this, "User with this email already exists", Toast.LENGTH_SHORT).show()
                }
                is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> {
                    Toast.makeText(this, "Weak password! Should be at least 6 characters", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.i(TAG, "EXCETION${user.exception}")
                }
            }
        }
    }

    fun finalizeSetup() { AuthRepository.proceedLoggedIn(this) }

    fun showSetupError() {

    }


}
