package gr.gkortsaridis.gatekeeper.UI.Settings


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.biometric.BiometricManager
import gr.gkortsaridis.gatekeeper.Entities.Login

import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.UI.Authentication.PinAuthenticationActivity
import info.hoang8f.android.segmented.SegmentedGroup

class SettingsFragment(private val activity: Activity) : Fragment() {

    private lateinit var authTypeSegmentedGroup: SegmentedGroup
    private lateinit var loginClickSegmentedGroup: SegmentedGroup
    private lateinit var bioAuth: RadioButton
    private lateinit var passAuth: RadioButton
    private lateinit var pinAuth: RadioButton
    private lateinit var pinError1: TextView
    private lateinit var pinError2: TextView
    private lateinit var authTypeDescription: TextView
    private lateinit var loginClickCopy: RadioButton
    private lateinit var loginClickOpen: RadioButton
    private lateinit var loginClickDescription: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        bioAuth = view.findViewById(R.id.auth_type_bio)
        passAuth = view.findViewById(R.id.auth_type_pass)
        pinAuth = view.findViewById(R.id.auth_type_pin)
        authTypeSegmentedGroup = view.findViewById(R.id.auth_type_segmented)
        pinError1 = view.findViewById(R.id.pin_error_1)
        pinError2 = view.findViewById(R.id.pin_error_2)
        authTypeDescription = view.findViewById(R.id.auth_type_description)
        loginClickSegmentedGroup = view.findViewById(R.id.login_click_action_segmented)
        loginClickCopy = view.findViewById(R.id.login_click_action_copy)
        loginClickOpen = view.findViewById(R.id.login_click_action_open)
        loginClickDescription = view.findViewById(R.id.login_click_action_description)

        authTypeSegmentedGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                passAuth.id -> { setPreferredAuthType(AuthRepository.PASSWORD_SIGN_IN) }
                bioAuth.id -> { setPreferredAuthType(AuthRepository.BIO_SIN_IN) }
                pinAuth.id -> { setPreferredAuthType(AuthRepository.PIN_SIGN_IN) }
            }
        }

        loginClickSegmentedGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                loginClickCopy.id -> { setPreferredLoginClickAction(LoginsRepository.LOGIN_CLICK_ACTION_COPY) }
                loginClickOpen.id -> { setPreferredLoginClickAction(LoginsRepository.LOGIN_CLICK_ACTION_OPEN) }
            }
        }

        pinError2.setOnClickListener {
            val intent = Intent(activity, PinAuthenticationActivity::class.java)
            intent.putExtra("MODE", "PIN_SETUP")
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {

        //AUTHENTICATION TYPE UI
        val preferredAuthType = AuthRepository.getPreferredAuthType()
        val canAuthenticateWithBio = BiometricManager.from(activity).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS

        authTypeDescription.visibility = View.VISIBLE
        pinError1.visibility = View.GONE
        pinError2.visibility = View.GONE

        if (preferredAuthType == AuthRepository.BIO_SIN_IN && canAuthenticateWithBio) {
            bioAuth.isChecked = true
            authTypeDescription.text = getString(R.string.auth_type_fingerprint_description)
        } else if (preferredAuthType == AuthRepository.PIN_SIGN_IN) {
            val pinSet = DataRepository.pinLock.isNotEmpty()
            pinAuth.isChecked = true
            pinError1.visibility = if (pinSet) View.GONE else View.VISIBLE
            pinError2.visibility = if (pinSet) View.GONE else View.VISIBLE
            authTypeDescription.visibility = if(pinSet) View.VISIBLE else View.GONE
            authTypeDescription.text = getString(R.string.auth_type_pin_description)
        } else {
            passAuth.isChecked = true
            authTypeDescription.text = getString(R.string.auth_type_password_description)
        }


        //LOGIN ACTION UI
        val loginClickAction = DataRepository.loginClickAction
        if (loginClickAction == LoginsRepository.LOGIN_CLICK_ACTION_COPY) {
            loginClickCopy.isChecked = true
            loginClickDescription.text = getString(R.string.login_action_copy)
        } else if (loginClickAction == LoginsRepository.LOGIN_CLICK_ACTION_OPEN) {
            loginClickOpen.isChecked = true
            loginClickDescription.text = getString(R.string.login_action_open)
        }
    }

    private fun setPreferredLoginClickAction(type: Int) {
        DataRepository.loginClickAction = type
        updateUI()
    }

    private fun setPreferredAuthType(type: Int) {
        DataRepository.preferredAuthType = type
        updateUI()
    }

}
