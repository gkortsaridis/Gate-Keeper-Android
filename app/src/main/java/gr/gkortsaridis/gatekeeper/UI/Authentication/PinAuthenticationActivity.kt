package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView
import gr.gkortsaridis.gatekeeper.Entities.FirebaseSignInResult
import gr.gkortsaridis.gatekeeper.Interfaces.SignInListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository

class PinAuthenticationActivity : AppCompatActivity() {

    private val activity = this
    private lateinit var lockView : PinLockView
    private lateinit var indicatorDots : IndicatorDots
    private lateinit var pinMessage : TextView
    private lateinit var goToPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_authentication)

        val setupMode = (intent.getStringExtra("MODE") == "PIN_SETUP")
        val savedCredentials = AuthRepository.loadCredentials()
        var tempPin = ""

        lockView = findViewById(R.id.pin_lock_view)
        indicatorDots = findViewById(R.id.indicator_dots)
        pinMessage = findViewById(R.id.pin_message)
        goToPassword = findViewById(R.id.go_to_pass_auth)

        if (setupMode) {
            goToPassword.visibility = View.GONE
            pinMessage.text = getString(R.string.please_setup_your_pin)
        } else {
            goToPassword.visibility = View.VISIBLE
            pinMessage.text = getString(R.string.welcome)
        }

        goToPassword.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        lockView.attachIndicatorDots(indicatorDots)
        lockView.setPinLockListener(object : PinLockListener{
            override fun onEmpty() {}

            override fun onComplete(pin: String?) {
                if (!setupMode) {
                    //Not in setup mode. Sign In

                    if (pin == DataRepository.pinLock) {
                        AuthRepository.signIn(activity, savedCredentials!!.email, savedCredentials!!.password, true, object: SignInListener{
                            override fun onSignInComplete(success: Boolean, user: FirebaseSignInResult) {
                                if (success) {
                                    AuthRepository.setApplicationUser(user.authResult!!.user!!)
                                    AuthRepository.proceedLoggedIn(activity)
                                }else {
                                    Toast.makeText(activity, user.exception.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onRegistrationNeeded(email: String) {  }
                        })
                    } else {
                        Toast.makeText(activity, "Wrong PIN entered", Toast.LENGTH_SHORT).show()
                        lockView.resetPinLockView()
                    }

                } else {
                    //In Setup Mode.
                    if (tempPin == "") {
                        tempPin = pin!!
                        Toast.makeText(activity,"Please repeat your PIN", Toast.LENGTH_SHORT).show()
                        lockView.resetPinLockView()
                    } else {
                      if (tempPin == pin!!) {
                          Toast.makeText(activity, "Your PIN is successfully setup", Toast.LENGTH_SHORT).show()
                          DataRepository.pinLock = pin
                          DataRepository.preferredAuthType = AuthRepository.PIN_SIGN_IN
                          finish()
                      }
                    }
                }

            }

            override fun onPinChange(pinLength: Int, intermediatePin: String?) {  }
        })
    }
}
