package gr.gkortsaridis.gatekeeper.Repositories

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.CompletableFuture

data class FirebaseSignInResult(val authResult: AuthResult?, val exception: Exception?)

object AuthRepository {

    val auth = FirebaseAuth.getInstance()
    /*val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(this, gso)*/

    fun signIn(email: String, password: String): FirebaseSignInResult {
        val response = CompletableFuture<FirebaseSignInResult>()
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { response.complete( FirebaseSignInResult(it, null)  )}
            .addOnFailureListener { response.complete( FirebaseSignInResult(null, it) )}
        return response.get()
    }


}