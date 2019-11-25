package gr.gkortsaridis.gatekeeper.UI.Authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.FolderSetupListener
import gr.gkortsaridis.gatekeeper.Interfaces.SignUpListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultSetupListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.FirebaseSignInResult
import gr.gkortsaridis.gatekeeper.Repositories.FolderRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import java.lang.Exception

class SignUpActivity : AppCompatActivity(), SignUpListener {

    private val TAG = "_Sign_Up_"

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var signUp: Button

    private var vaultSetup = false
    private var folderSetup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        email = findViewById(R.id.emailET)
        password = findViewById(R.id.passwordET)
        signUp = findViewById(R.id.sign_up)

        signUp.setOnClickListener { signUp(email.text.toString(), password.text.toString())}
    }

    private fun signUp(email: String, password: String) {
        AuthRepository.signUp(this, email,password, this)
    }

    override fun onSignUpComplete(success: Boolean, user: FirebaseSignInResult) {
        if (success) {
            AuthRepository.setApplicationUser(user.authResult!!.user!!)
            VaultRepository.setupVaultsForNewUser(GateKeeperApplication.user, object: VaultSetupListener {
                override fun onVaultSetupComplete() {
                    vaultSetup = true
                    finalizeSetup()
                }
                override fun onVaultSetupError() { showSetupError() }
            })

            FolderRepository.setupFoldersForNewUser(GateKeeperApplication.user, object: FolderSetupListener {
                override fun onFolderSetupComplete() {
                    folderSetup = true
                    finalizeSetup()
                }

                override fun onFolderSetupError() {
                    showSetupError()
                }
            })
        }else{
            when (user.exception) {
                is com.google.firebase.auth.FirebaseAuthUserCollisionException -> {
                    Toast.makeText(this, "User with this email already exists", Toast.LENGTH_SHORT).show()
                }
                is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> {
                    Toast.makeText(this, "Weak password! Should be at least 6 characters", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.i(TAG, "EXCETION${user.exception}")
                }
            }
        }
    }

    fun finalizeSetup() { if (vaultSetup && folderSetup ) { AuthRepository.proceedLoggedIn(this) } }

    fun showSetupError() {

    }


}
