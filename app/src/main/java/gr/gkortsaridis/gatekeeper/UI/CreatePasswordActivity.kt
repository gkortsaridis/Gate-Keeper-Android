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

class CreatePasswordActivity : AppCompatActivity() {

    private val TAG = "_Create_Password_Activity_"

    private lateinit var password : EditText
    private lateinit var repeatPassword: EditText
    private lateinit var setPassword: Button
    private lateinit var email: String

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_password)

        auth = FirebaseAuth.getInstance()
        email = "gkortsaridis@gmail.com" //TODO: get!

        password = findViewById(R.id.passwordET)
        repeatPassword = findViewById(R.id.repeatPasswordET)
        setPassword = findViewById(R.id.set_password)

        setPassword.setOnClickListener { setPassword() }

    }

    private fun setPassword() {
        if (password.text.toString().equals(repeatPassword.text.toString())) {

            auth.createUserWithEmailAndPassword(email, password.text.toString()).addOnSuccessListener { result: AuthResult ->
                Log.i(TAG, "SUCCESS: $result")
            }.addOnFailureListener {e: Exception ->
                if (e is com.google.firebase.auth.FirebaseAuthWeakPasswordException) {
                    Toast.makeText(this, "Weak password! Should be at least 6 characters", Toast.LENGTH_SHORT).show()
                }

                Log.i(TAG, "EXCETION$e")
            }

        }
    }
}
