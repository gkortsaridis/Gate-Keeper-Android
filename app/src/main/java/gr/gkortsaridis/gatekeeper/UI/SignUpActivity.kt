package gr.gkortsaridis.gatekeeper.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import gr.gkortsaridis.gatekeeper.R
import java.lang.Exception

class SignUpActivity : AppCompatActivity() {

    private val TAG = "_Sign_Up_"

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var signUp: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        email = findViewById(R.id.emailET)
        password = findViewById(R.id.passwordET)
        signUp = findViewById(R.id.sign_up)

        signUp.setOnClickListener { signUp(email.text.toString(), password.text.toString())}

        auth = FirebaseAuth.getInstance()
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { result: AuthResult ->
            Log.i(TAG, "SUCCESS: $result")
        }.addOnFailureListener {e: Exception ->
            if (e is com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                Toast.makeText(this, "User with this email already exists", Toast.LENGTH_SHORT).show()
            }else if (e is com.google.firebase.auth.FirebaseAuthWeakPasswordException) {
                Toast.makeText(this, "Weak password! Should be at least 6 characters", Toast.LENGTH_SHORT).show()
            }

            Log.i(TAG, "EXCETION$e")
        }
    }
}
