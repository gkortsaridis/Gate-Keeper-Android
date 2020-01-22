package gr.gkortsaridis.gatekeeper.UI.Authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository

class PinAuthenticationActivity : AppCompatActivity() {

    private lateinit var lockView : PinLockView
    private lateinit var indicatorDots : IndicatorDots

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_authentication)

        lockView = findViewById(R.id.pin_lock_view)
        indicatorDots = findViewById(R.id.indicator_dots)

        lockView.attachIndicatorDots(indicatorDots)

        lockView.setPinLockListener(object : PinLockListener{
            override fun onEmpty() {
                Log.i("PIN LOCK", "EMPTY")
            }

            override fun onComplete(pin: String?) {
                Log.i("PIN LOCK", "Complete $pin")
            }

            override fun onPinChange(pinLength: Int, intermediatePin: String?) {
                Log.i("PIN LOCK", "PIN CHANGE $intermediatePin $pinLength")
            }
        })
    }
}
