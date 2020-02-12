package gr.gkortsaridis.gatekeeper.UI.Logins


import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
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
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.stone.vega.library.VegaLayoutManager
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginSelectListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository.LOGIN_SORT_TYPE_NAME
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository.createLoginRequestCode
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository.createLoginSuccess
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository.deleteLoginSuccess
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.LoginsRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import gr.gkortsaridis.gatekeeper.Utils.dp
import io.noties.tumbleweed.Timeline
import io.noties.tumbleweed.Tween
import io.noties.tumbleweed.android.ViewTweenManager
import io.noties.tumbleweed.android.types.Alpha
import io.noties.tumbleweed.android.types.Translation
import io.noties.tumbleweed.equations.Cubic


class LoginsFragment() : Fragment(), LoginSelectListener {
    private val TAG = "_LOGINS_FRAGMENT_"

    private lateinit var loginsRV: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var vaultName: TextView
    private lateinit var vaultView: LinearLayout
    private lateinit var addLoginButton: Button
    private lateinit var noLoginsMessage: LinearLayout
    private lateinit var loginsCounterContainer: LinearLayout
    private lateinit var loginCount: TextView
    private lateinit var loginsSortType: TextView
    private lateinit var loginsSortBtn: ImageButton


    private lateinit var mAdView: AdView
    private val mAppUnitId: String by lazy { "ca-app-pub-4492385836648698~3680446633" }

    private var autofillManager: AutofillManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_logins, container, false)

        loginsRV = view.findViewById(R.id.logins_recycler_view) as RecyclerView
        loginsRV.layoutManager = VegaLayoutManager()
        noLoginsMessage = view.findViewById(R.id.no_items_view)
        addLoginButton = view.findViewById(R.id.add_login_btn)
        fab = view.findViewById(R.id.fab)
        vaultView = view.findViewById(R.id.vault_view)
        vaultName = view.findViewById(R.id.vault_name)
        loginsCounterContainer = view.findViewById(R.id.logins_counter_container)
        loginCount = view.findViewById(R.id.login_cnt)
        loginsSortType = view.findViewById(R.id.logins_sort_type)
        loginsSortBtn = view.findViewById(R.id.sort_logins)
        mAdView = view.findViewById(R.id.logins_adview)

        MobileAds.initialize(context!!, mAppUnitId)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        addLoginButton.setOnClickListener { startActivityForResult(Intent(activity, CreateLoginActivity::class.java), createLoginRequestCode) }
        fab.setOnClickListener{ startActivityForResult(Intent(activity, CreateLoginActivity::class.java), createLoginRequestCode) }
        vaultView.setOnClickListener{
            val intent = Intent(activity, SelectVaultActivity::class.java)
            intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_ACTIVE_VAULT)
            intent.putExtra("vault_id",VaultRepository.getLastActiveVault().id)
            startActivityForResult(intent, GateKeeperConstants.CHANGE_ACTIVE_VAULT_REQUEST_CODE)
        }

        val sortTypes = arrayOf("Alias", "Modified date")
        loginsSortBtn.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Sort by")
            builder.setSingleChoiceItems(sortTypes, DataRepository.loginSortType) { dialog, which ->
                DataRepository.loginSortType = which
                updateUI()
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        animateFabIn()
        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val logins = LoginsRepository.filterLoginsByCurrentVault(GateKeeperApplication.logins)
        val sortType = DataRepository.loginSortType
        if (sortType == LOGIN_SORT_TYPE_NAME) {
            logins.sortBy { it.name.toLowerCase() }
            loginsSortType.text = "passwords, sort by alias"
        }
        else {
            logins.sortBy { it.date_modified }
            loginsSortType.text = "passwords, sort by modified date"
        }

        loginsRV.adapter =
            LoginsRecyclerViewAdapter(
                activity!!.baseContext,
                logins,
                activity!!.packageManager,
                this
            )

        loginCount.text = logins.size.toString()

        vaultName.text = VaultRepository.getLastActiveVault().name
        noLoginsMessage.visibility = if (logins.size > 0) View.GONE else View.VISIBLE
        fab.visibility = if (logins.size > 0) View.VISIBLE else View.GONE
        loginsCounterContainer.visibility = if (logins.size > 0) View.VISIBLE else View.GONE
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

    private fun animateFabIn() {
        Timeline.createParallel()
            .push(Tween.to(fab, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(fab, Translation.XY).target(0f,-122.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(fab))
    }
}
