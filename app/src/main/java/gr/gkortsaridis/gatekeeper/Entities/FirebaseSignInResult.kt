package gr.gkortsaridis.gatekeeper.Entities

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import java.lang.Exception

data class FirebaseSignInResult(val authResult: AuthResult?, val exception: Exception?)
