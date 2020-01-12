package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import gr.gkortsaridis.gatekeeper.Entities.FirebaseSignInResult
import gr.gkortsaridis.gatekeeper.Interfaces.SignInListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository.RC_SIGN_IN
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import gr.gkortsaridis.gatekeeper.Utils.pbkdf2_lib
import kotlinx.android.synthetic.main.activity_login.*
import javax.crypto.spec.SecretKeySpec


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
            password.setText(loadedCredentials.password)
        }else{
            saveCredentials.isChecked = false
        }


        encryption()
    }

    private fun signUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun signIn(email: String, password: String, check: Boolean) {
        AuthRepository.signIn(this, email, password, check,this)
    }

    private fun googleSignIn() {
        AuthRepository.googleSignIn(this)
    }

    override fun onSignInComplete(success: Boolean, user: FirebaseSignInResult) {
        if (success) {
            val biometricManager = BiometricManager.from(this)

            if (AuthRepository.getPreferredAuthType() == AuthRepository.signInNotSet
                && biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                AlertDialog.Builder(this)
                    .setTitle("Biometric Sign In")
                    .setMessage("Would you like to use our biometric authentication feature?\nYour credentials are going to be safely stored on the device.")
                    .setPositiveButton("Yes") { _, _ ->
                        DataRepository.preferredAuthType = AuthRepository.bioSignIn
                        saveCredentials.isChecked = true
                        proceedLogin(user)
                    }
                    .setNegativeButton("No") { _, _ ->
                        DataRepository.preferredAuthType = AuthRepository.passwordSignIn
                        proceedLogin(user)
                    }
                    .show()
            } else {
                proceedLogin(user)
            }

        } else {
            Toast.makeText(this, "Wrong Credentials", Toast.LENGTH_SHORT).show()
        }
    }

    private fun proceedLogin(user: FirebaseSignInResult) {
        if (saveCredentials.isChecked) { AuthRepository.saveCredentials(email = email.text.toString(), password = password.text.toString()) }
        else { AuthRepository.clearCredentials() }

        AuthRepository.setApplicationUser(user.authResult!!.user!!)
        AuthRepository.proceedLoggedIn(this)
    }

    override fun onRegistrationNeeded(email: String) {
        startActivity(Intent(this, CreatePasswordActivity::class.java))
    }

    //Pretty much onGoogleSignInComplete
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account?.email != null) {
                    signIn(account.email!!, account.id!!, true)
                }
            } catch (e: ApiException) {
                Log.w("LOGIN ACTIVITY", "Google sign in failed", e)
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    val username = "aspisteam@gmail.com"
    val pass = "aspisteam"

    private fun encryption() {

        val ek = pbkdf2_lib.createHash(pass, username)
        val originalKey = SecretKeySpec(ek, "AES")

        val enc_data = SecurityRepository.encryptWithKey("Hello World" ,originalKey)
        val data = Base64.encodeToString(enc_data!!.encryptedData, Base64.DEFAULT)
        val iv = Base64.encodeToString(enc_data.iv, Base64.DEFAULT)


        val decrypted = SecurityRepository.decryptWithKey(Base64.decode(data, Base64.DEFAULT), Base64.decode(iv, Base64.DEFAULT), originalKey)

        Log.i("DECRTPTED", decrypted)
    }
}
