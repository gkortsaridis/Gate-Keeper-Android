package gr.gkortsaridis.gatekeeper.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginCreateListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository

class CreateLoginActivity : AppCompatActivity() {

    private val TAG = "_Create_Login_Activity_"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_login)

        val loginObj = Login()
        LoginsRepository.encryptAndStoreLogin(loginObj, object : LoginCreateListener{
            override fun onLoginCreated() {

            }

            override fun onLoginCreateError() {

            }
        })
    }
}
