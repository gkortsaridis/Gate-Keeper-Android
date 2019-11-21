package gr.gkortsaridis.gatekeeper.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R

class CreateLoginActivity : AppCompatActivity() {

    private val TAG = "_Create_Login_Activity_"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_login)

        val loginObj = Login()
        val encryptedLogin = loginObj.encrypt()

        val login = hashMapOf(
            "login" to encryptedLogin,
            "account_id" to GateKeeperApplication.userAccount.id
        )

        val db = FirebaseFirestore.getInstance()

// Add a new document with a generated ID
        db.collection("logins")
            .add(login)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }


    }
}
