package gr.gkortsaridis.gatekeeper.UI.Logins


import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.autofill.AutofillManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginSelectListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository.createLoginRequestCode
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository.createLoginSuccess
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository.deleteLoginSuccess
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.LoginsRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants


class LoginsFragment(private var activity: Activity) : Fragment(), LoginSelectListener {

    private val TAG = "_LOGINS_FRAGMENT_"

    private lateinit var loginsRV: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var vaultName: TextView
    private lateinit var vaultView: LinearLayout
    private lateinit var noLoginsMessage: TextView
    private var autofillManager: AutofillManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_logins, container, false)

        //RecyclerView Initialization
        loginsRV = view.findViewById(R.id.logins_recycler_view) as RecyclerView
        loginsRV.layoutManager = LinearLayoutManager(activity)
        noLoginsMessage = view.findViewById(R.id.no_logins_message)

        fab = view.findViewById(R.id.fab)
        vaultView = view.findViewById(R.id.vault_view)
        vaultName = view.findViewById(R.id.vault_name)

        fab.setOnClickListener{ startActivityForResult(Intent(activity, CreateLoginActivity::class.java), createLoginRequestCode)}
        vaultView.setOnClickListener{
            val intent = Intent(activity, SelectVaultActivity::class.java)
            intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_ACTIVE_VAULT)
            intent.putExtra("vault_id",VaultRepository.getLastActiveVault().id)
            startActivityForResult(intent, GateKeeperConstants.CHANGE_ACTIVE_VAULT_REQUEST_CODE)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val logins = LoginsRepository.filterLoginsByCurrentVault(GateKeeperApplication.logins)
        loginsRV.adapter =
            LoginsRecyclerViewAdapter(
                activity.baseContext,
                logins,
                activity.packageManager,
                this
            )

        vaultName.text = VaultRepository.getLastActiveVault().name
        noLoginsMessage.visibility = if (logins.size > 0) View.GONE else View.VISIBLE
    }

    private fun checkForAutofill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            autofillManager = context?.getSystemService(AutofillManager::class.java)

            if (!autofillManager!!.hasEnabledAutofillServices()) {
                AlertDialog.Builder(context)
                    .setTitle("Autofill is not enabled")
                    .setMessage("Gate Keeper can fill your saved credentials to applications. Would you like to enable that functionality?")
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, which ->
                        val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
                        intent.data = Uri.parse("package:gr.gkortsaridis.gatekeeper.GateKeeperAutoFillServiceL")
                        startActivity(intent)
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == createLoginRequestCode && resultCode == createLoginSuccess) {
            updateUI()
            Toast.makeText(context, "Login successfully created", Toast.LENGTH_SHORT).show()
        } else if (resultCode == deleteLoginSuccess) {
            updateUI()
            Toast.makeText(context, "Login successfully deleted", Toast.LENGTH_SHORT).show()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onLoginClicked(login: Login) {
        val action = DataRepository.loginClickAction
        if (action == LoginsRepository.LOGIN_CLICK_ACTION_OPEN) { openLogin(login) }
        else { copyLoginPassword(login) }
    }

    override fun onLoginActionClicked(login: Login) {
        val action = DataRepository.loginClickAction
        if (action == LoginsRepository.LOGIN_CLICK_ACTION_OPEN) { copyLoginPassword(login) }
        else { openLogin(login) }
    }

    private fun openLogin(login: Login) {
        Log.i("Clicked", login.name)
        val intent = Intent(activity, CreateLoginActivity::class.java)
        intent.putExtra("login_id",login.id)
        startActivity(intent)
    }

    private fun copyLoginPassword(login: Login) {
        val clipboard = getSystemService(
            context!!,
            ClipboardManager::class.java
        ) as ClipboardManager
        val clip = ClipData.newPlainText("label",login.password)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, login.name+" password copied", Toast.LENGTH_SHORT).show()
    }
}
