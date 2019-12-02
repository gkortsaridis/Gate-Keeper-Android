package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import gr.gkortsaridis.gatekeeper.Entities.FirebaseSignInResult
import gr.gkortsaridis.gatekeeper.Interfaces.SignInListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository.RC_SIGN_IN
import kotlinx.android.synthetic.main.activity_login.emailET


class LoginActivity : AppCompatActivity(), SignInListener {

    private val TAG = "_Login_Activity_"

    private lateinit var googleSignIn: SignInButton
    private lateinit var signIn: Button
    private lateinit var signUpLink: TextView
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var saveCredentials: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        googleSignIn = findViewById(R.id.sign_in_google)
        signIn = findViewById(R.id.sign_in)
        signUpLink = findViewById(R.id.sign_up_link)
        email = findViewById(R.id.emailET)
        password = findViewById(R.id.passwordET)
        saveCredentials = findViewById(R.id.save_credentials)

        googleSignIn.setOnClickListener { googleSignIn() }
        signIn.setOnClickListener { signIn(emailET.text.toString(), password.text.toString(), false) }
        signUpLink.setOnClickListener { signUp() }

        val loadedCredentials = AuthRepository.loadCredentials()
        if (loadedCredentials != null) {
            email.setText(loadedCredentials.email)
            password.setText(loadedCredentials.password)
        }

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
            if (saveCredentials.isChecked) { AuthRepository.saveCredentials(email = email.text.toString(), password = password.text.toString()) }

            AuthRepository.setApplicationUser(user.authResult!!.user!!)
            AuthRepository.proceedLoggedIn(this)
        }else{
            Toast.makeText(this, "Wrong Credentials", Toast.LENGTH_SHORT).show()
        }
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


    private fun proceedAuthenticated() {
        val intent = Intent(this, LoadingActivity::class.java)
        startActivity(intent)
    }
}