package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import gr.gkortsaridis.gatekeeper.Interfaces.SignInListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), SignInListener {

    private val TAG = "_Login_Activity_"

    private lateinit var signIn: Button
    private lateinit var signUpLink: TextView
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var saveCredentials: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
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
    }

    private fun proceedLogin(user: String) {
        //Save credentials locally for decryption
        AuthRepository.saveCredentials(email = email.text.toString(), password = password.text.toString())

        AuthRepository.setApplicationUser(user)
        AuthRepository.proceedLoggedIn(this)
    }
}
