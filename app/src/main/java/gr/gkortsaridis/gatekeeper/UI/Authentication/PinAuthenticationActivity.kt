package gr.gkortsaridis.gatekeeper.UI.Authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_authentication)

        val setupMode = (intent.getStringExtra("MODE") == "PIN_SETUP")
        val savedCredentials = AuthRepository.loadCredentials()
        var tempPin = ""

        lockView = findViewById(R.id.pin_lock_view)
        indicatorDots = findViewById(R.id.indicator_dots)
        pinMessage = findViewById(R.id.pin_message)

        if (setupMode) {
            pinMessage.text = getString(R.string.please_setup_your_pin)
        } else {
            pinMessage.text = getString(R.string.welcome)
        }

        lockView.attachIndicatorDots(indicatorDots)
        lockView.setPinLockListener(object : PinLockListener{
            override fun onEmpty() {}

            override fun onComplete(pin: String?) {
                if (!setupMode) {
                    //Not in setup mode. Sign In
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
                    //In Setup Mode.
                    if (tempPin == "") {
                        tempPin = pin!!
                        Toast.makeText(activity,"Please repeat your PIN", Toast.LENGTH_SHORT).show()
                        lockView.resetPinLockView()
                    } else {
                      if (tempPin == pin!!) {
                          DataRepository.pinLock = pin
                          Toast.makeText(activity, "Your PIN is successfully setup", Toast.LENGTH_SHORT).show()
                          finish()
                      }
                    }
                }

            }

            override fun onPinChange(pinLength: Int, intermediatePin: String?) {  }
        })
    }
}