package gr.gkortsaridis.gatekeeper.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R


class LoginActivity : AppCompatActivity() {

    private val TAG = "_Login_Activity_"

    private lateinit var googleSignIn: SignInButton
    private lateinit var signIn: Button
    private lateinit var signUpLink: TextView

    private lateinit var auth : FirebaseAuth
    private lateinit var gso : GoogleSignInOptions
    private lateinit var googleSignInClient : GoogleSignInClient
    private var RC_SIGN_IN : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        googleSignIn = findViewById(R.id.sign_in_google)
        signIn = findViewById(R.id.sign_in)
        signUpLink = findViewById(R.id.sign_up_link)

        googleSignIn.setOnClickListener { googleSignIn() }
        signIn.setOnClickListener { signIn("gkortsaridis@gmail.com", "1234567890") }
        signUpLink.setOnClickListener { signUp() }

        auth = FirebaseAuth.getInstance()

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        /*val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account == null) {

        }else {
            GateKeeperApplication.userAccount = account
            //TODO: For for, just proceed to logins screen. Later, create PIN/Bio sign in
            //proceedAuthenticated()
        }*/

    }

    private fun signUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)


                //GateKeeperApplication.userAccount = account!!
                //proceedAuthenticated()
            } catch (e: ApiException) {
                Log.w("LOGIN ACTIVITY", "Google sign in failed", e)
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkSignedUp(email: String) {
        signIn(email,"TEST_PASS");
    }

    private fun proceedAuthenticated() {
        val intent = Intent(this, LoadingActivity::class.java)
        startActivity(intent)
    }
}
