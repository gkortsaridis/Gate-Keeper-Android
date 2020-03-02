package gr.gkortsaridis.gatekeeper.UI.Settings


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.biometric.BiometricManager

import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.UI.Authentication.PinAuthenticationActivity
import info.hoang8f.android.segmented.SegmentedGroup

class SettingsFragment : Fragment() {

    private lateinit var authTypeSegmentedGroup: SegmentedGroup
    private lateinit var loginClickSegmentedGroup: SegmentedGroup
    private lateinit var bioAuth: RadioButton
    private lateinit var passAuth: RadioButton
    private lateinit var pinAuth: RadioButton
    private lateinit var authTypeDescription: TextView
    private lateinit var loginClickCopy: RadioButton
    private lateinit var loginClickOpen: RadioButton
    private lateinit var loginClickDescription: TextView
    private lateinit var setupPin: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        bioAuth = view.findViewById(R.id.auth_type_bio)
        passAuth = view.findViewById(R.id.auth_type_pass)
        pinAuth = view.findViewById(R.id.auth_type_pin)
        authTypeSegmentedGroup = view.findViewById(R.id.auth_type_segmented)
        authTypeDescription = view.findViewById(R.id.auth_type_description)
        loginClickSegmentedGroup = view.findViewById(R.id.login_click_action_segmented)
        loginClickCopy = view.findViewById(R.id.login_click_action_copy)
        loginClickOpen = view.findViewById(R.id.login_click_action_open)
        loginClickDescription = view.findViewById(R.id.login_click_action_description)
        setupPin = view.findViewById(R.id.setup_pin)

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

        setupPin.setOnClickListener { setupPin() }
        return view
    }

    override fun onResume() {
        super.onResume()
        setupSegmentedControl()
        updateUI()
    }

    private fun setupSegmentedControl() {
        val canAuthenticateWithBio = BiometricManager.from(activity!!).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS

        bioAuth.visibility = if (canAuthenticateWithBio) View.VISIBLE else View.GONE
        authTypeSegmentedGroup.weightSum = if (canAuthenticateWithBio) 3f else 2f

        if (!canAuthenticateWithBio && DataRepository.preferredAuthType == AuthRepository.PIN_SIGN_IN) {
            DataRepository.preferredAuthType = AuthRepository.PASSWORD_SIGN_IN
        }
    }

    private fun updateUI() {

        //AUTHENTICATION TYPE UI
        val preferredAuthType = AuthRepository.getPreferredAuthType()
        val canAuthenticateWithBio = BiometricManager.from(activity!!).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS

        authTypeDescription.visibility = View.VISIBLE
        setupPin.visibility = View.GONE

        if (preferredAuthType == AuthRepository.BIO_SIN_IN && canAuthenticateWithBio) {
            bioAuth.isChecked = true
            authTypeDescription.text = getString(R.string.auth_type_biometric_description)
        } else if (preferredAuthType == AuthRepository.PIN_SIGN_IN) {
            val pinSet = DataRepository.pinLock.isNotEmpty()
            pinAuth.isChecked = true
            setupPin.visibility = View.VISIBLE
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

    private fun setupPin() {
        val intent = Intent(activity, PinAuthenticationActivity::class.java)
        intent.putExtra("MODE", "PIN_SETUP")
        startActivity(intent)
    }


    private fun setPreferredLoginClickAction(type: Int) {
        DataRepository.loginClickAction = type
        updateUI()
    }

    private fun setPreferredAuthType(type: Int) {
        if (type == AuthRepository.PIN_SIGN_IN && DataRepository.pinLock.isNullOrEmpty()) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("GateKeeper PIN not set")
            builder.setMessage("Your PIN is not set yet. Please setup a PIN if you want to use this authentication method")
            builder.setPositiveButton("Setup") { dialog, which -> setupPin() }
            builder.setNegativeButton("Cancel") { dialog,which -> setPreferredAuthType(DataRepository.preferredAuthType) }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else {
            DataRepository.preferredAuthType = type
            updateUI()
        }
    }

}
