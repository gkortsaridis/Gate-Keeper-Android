package gr.gkortsaridis.gatekeeper.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R


class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignIn: SignInButton

    private lateinit var gso : GoogleSignInOptions
    private lateinit var googleSignInClient : GoogleSignInClient
    private var RC_SIGN_IN : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        googleSignIn = findViewById(R.id.sign_in_button)
        googleSignIn.setOnClickListener { signIn() }


        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account == null) {


        }else {
            GateKeeperApplication.userAccount = account
            //TODO: For for, just proceed to logins screen. Later, create PIN/Bio sign in
            //proceedAuthenticated()
        }

    }

    private fun signIn() {
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
                GateKeeperApplication.userAccount = account!!
                proceedAuthenticated()
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
