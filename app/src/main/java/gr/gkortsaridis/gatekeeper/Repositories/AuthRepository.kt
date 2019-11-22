package gr.gkortsaridis.gatekeeper.Repositories

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.SignInListener
import gr.gkortsaridis.gatekeeper.Interfaces.SignUpListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.LoadingActivity
import gr.gkortsaridis.gatekeeper.UI.MainActivity

data class FirebaseSignInResult(val authResult: AuthResult?, val exception: Exception?)

object AuthRepository {

    private val TAG = "_Auth_Repository_"

    val auth = FirebaseAuth.getInstance()
    val RC_SIGN_IN : Int = 1


    fun signIn(activity:Activity, email: String, password: String, check: Boolean, listener: SignInListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                viewDialog.hideDialog()
                when {
                    it.isSuccessful -> {
                        listener.onSignInComplete(true, FirebaseSignInResult(it.result,null))
                    }
                    check -> {
                        listener.onRegistrationNeeded(email)
                    }
                    else -> {
                        listener.onSignInComplete(false, FirebaseSignInResult(null,it.exception))
                    }
                }
            }
    }

    fun googleSignIn(activity: Activity) {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(activity, gso)

        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signUp(activity: Activity, email: String, password: String, listener: SignUpListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { result: AuthResult ->
            viewDialog.hideDialog()
            listener.onSignUpComplete(true, FirebaseSignInResult(result, null))
        }.addOnFailureListener {e: java.lang.Exception ->
            viewDialog.hideDialog()
            listener.onSignUpComplete(false, FirebaseSignInResult(null, e))
        }
    }

    fun proceedLoggedIn(activity: Activity, user: FirebaseUser) {
        Log.i(TAG, "PROCEED AUTHENTICATED FOR: "+user.uid)
        GateKeeperApplication.user = user
        activity.startActivity(Intent(activity, LoadingActivity::class.java))
    }

}