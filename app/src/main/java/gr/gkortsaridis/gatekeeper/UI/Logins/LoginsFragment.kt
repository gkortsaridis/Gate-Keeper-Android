package gr.gkortsaridis.gatekeeper.UI.Logins


import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.autofill.AutofillManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
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
import kotlinx.android.synthetic.main.fragment_logins.*


class LoginsFragment() : Fragment(), LoginSelectListener {

    private val TAG = "_LOGINS_FRAGMENT_"
    private var autofillManager: AutofillManager? = null
    private lateinit var loginsAdapter: LoginsRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_logins, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adRequest = AdRequest.Builder().build()
        adview.loadAd(adRequest)

        logins_recycler_view.layoutManager = VegaLayoutManager()

        add_login_btn.setOnClickListener { startActivityForResult(Intent(activity, CreateLoginActivity::class.java), createLoginRequestCode) }
        fab.setOnClickListener{ startActivityForResult(Intent(activity, CreateLoginActivity::class.java), createLoginRequestCode) }
        vault_view.setOnClickListener{
            val intent = Intent(activity, SelectVaultActivity::class.java)
            intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_ACTIVE_VAULT)
            intent.putExtra("vault_id",VaultRepository.getLastActiveVault().id)
            startActivityForResult(intent, GateKeeperConstants.CHANGE_ACTIVE_VAULT_REQUEST_CODE)
        }

        val sortTypes = arrayOf("Alias", "Modified date")
        sort_logins.setOnClickListener {
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

        loginsAdapter =
            LoginsRecyclerViewAdapter(
                activity!!.baseContext,
                arrayListOf(), //empty for now
                activity!!.packageManager,
                this
            )
        logins_recycler_view.adapter = loginsAdapter

        animateItemsIn()
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
            logins_sort_type.text = "passwords, sort by alias"
        }
        else {
            logins.sortBy { it.date_modified }
            logins.reverse()
            logins_sort_type.text = "passwords, sort by modified date"
        }

        loginsAdapter.updateLogins(logins)

        login_cnt.text = logins.size.toString()

        val vault = VaultRepository.getLastActiveVault()
        vault_name.text = vault.name
        vault_view.setBackgroundColor(resources.getColor(vault.getVaultColorResource()))
        vault_name.setTextColor(resources.getColor(vault.getVaultColorAccent()))
        vault_icon.setColorFilter(resources.getColor(vault.getVaultColorAccent()))

        no_items_view.visibility = if (logins.size > 0) View.GONE else View.VISIBLE
        fab.visibility = if (logins.size > 0) View.VISIBLE else View.GONE
        logins_counter_container.visibility = if (logins.size > 0) View.VISIBLE else View.GONE
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

    private fun animateItemsIn() {
        Timeline.createParallel()
            .push(Tween.to(fab, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(fab, Translation.XY).target(0f,-162.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .push(Tween.to(adview_container, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(adview_container, Translation.XY).target(0f,-90.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(fab))
    }
}
