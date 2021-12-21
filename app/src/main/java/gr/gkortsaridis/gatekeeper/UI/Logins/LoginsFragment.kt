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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.stone.vega.library.VegaLayoutManager
import gr.gkortsaridis.gatekeeper.Database.MainViewModel
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginSelectListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AnalyticsRepository
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
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme
import io.noties.tumbleweed.Timeline
import io.noties.tumbleweed.Tween
import io.noties.tumbleweed.android.ViewTweenManager
import io.noties.tumbleweed.android.types.Alpha
import io.noties.tumbleweed.android.types.Translation
import io.noties.tumbleweed.equations.Cubic
import kotlinx.android.synthetic.main.fragment_logins.*
import androidx.compose.foundation.lazy.items


class LoginsFragment() : Fragment() {

    private val TAG = "_LOGINS_FRAGMENT_"
    private var autofillManager: AutofillManager? = null
    private var loginsAdapter: LoginsRecyclerViewAdapter? = null

    private var activeLogins: ArrayList<Login> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent { loginsPage() }
        }
        //return inflater.inflate(R.layout.fragment_logins, container, false)
    }

    @Preview
    @Composable
    fun loginsPage() {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(GateKeeperTheme.light_grey)
        ) {
            vaultSelector()
            itemsCount()
            itemsList()
        }

    }

    @Composable
    fun vaultSelector(){

        val bottomCornerCutShape = GenericShape { size, _ ->
            moveTo(0f,0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - 10.dp.value)
            lineTo(size.width - 10.dp.value, size.height)
            lineTo(10.dp.value, size.height)
            lineTo(0f, size.height - 10.dp.value)
        }


        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Card(
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth(),
                shape = bottomCornerCutShape,
                backgroundColor = GateKeeperTheme.colorPrimaryDark,
                elevation = 4.dp
            ) {}

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .height(56.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                backgroundColor = GateKeeperTheme.vault_red_1,
                elevation = 5.dp,
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 32.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.vault),
                        contentDescription = "Localized description",
                        colorFilter = ColorFilter.tint(GateKeeperTheme.white),
                        modifier = Modifier
                            .size(30.dp, 30.dp)
                    )

                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "Vault Name",
                        color = GateKeeperTheme.white,
                        fontSize = 19.sp
                    )
                }
            }


        }
    }

    @Composable
    fun itemsCount(){
        Row(
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "108",
                fontWeight = FontWeight.Bold,
                color = GateKeeperTheme.boldText,
                fontSize = 15.sp
            )

            Text(
                text = "items",
                color = GateKeeperTheme.greyText,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))


            Text(
                text = "Sort by name",
                color = GateKeeperTheme.greyText,
                fontSize = 15.sp,
            )

            Image(
                painter = painterResource(id = R.drawable.down_arrow_bold),
                contentDescription = "Localized description",
                modifier = Modifier
                    .size(24.dp, 24.dp)
            )

        }
    }

    @Composable
    fun itemsList(){

        val login1 = Login(account_id = "1", name = "My login", username = "gkortsaridis@gmail.com", password = "pass", url = "www.google.com", notes = "Some Notes", date_created=123L, date_modified=234L, vault_id="123")
        val logins = listOf(login1, login1, login1)

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 8.dp)
        ) {
            items(logins) { login -> loginItem(login = login)}
        }
    }

    @Composable
    fun loginItem(login: Login) {
        Card(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(80.dp),
            elevation = 4.dp,
            shape = RoundedCornerShape(10.dp),
            backgroundColor = GateKeeperTheme.white,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(16.dp)
                        .background(GateKeeperTheme.vault_red_1),
                )

                Image(
                    painter = painterResource(id = R.drawable.question_mark),
                    contentDescription = "Localized description",
                    modifier = Modifier
                        .size(40.dp, 40.dp)
                        .padding(4.dp)
                )

            }
        }
    }



    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adRequest = AdRequest.Builder().build()
        adview.loadAd(adRequest)

        val viewModel: MainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.appLogins.observe(requireActivity(), Observer {
            this.activeLogins = ArrayList(it)
            updateUI(this.activeLogins)
        })

        logins_recycler_view.layoutManager = VegaLayoutManager()

        add_login_btn.setOnClickListener { startActivityForResult(Intent(activity, CreateLoginActivity::class.java), createLoginRequestCode) }
        fab.setOnClickListener{ startActivityForResult(Intent(activity, CreateLoginActivity::class.java), createLoginRequestCode) }
        vault_view.setOnClickListener{
            val intent = Intent(activity, SelectVaultActivity::class.java)
            intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_ACTIVE_VAULT)
            intent.putExtra("vault_id",VaultRepository.getLastActiveVault().id)
            startActivityForResult(intent, GateKeeperConstants.CHANGE_ACTIVE_VAULT_REQUEST_CODE)
        }

        val sortTypes = arrayOf("Name", "Modified date")
        sort_logins.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Sort by")
            builder.setSingleChoiceItems(sortTypes, DataRepository.loginSortType) { dialog, which ->
                DataRepository.loginSortType = which
                updateUI(this.activeLogins)
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

        adview_container.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        updateUI(this.activeLogins)
    }

    private fun updateUI(allLogins: ArrayList<Login>) {
        val logins = LoginsRepository.filterLoginsByCurrentVault(allLogins)
        val sortType = DataRepository.loginSortType
        if (sortType == LOGIN_SORT_TYPE_NAME) {
            logins.sortBy { it.name.toLowerCase() }
            logins_sort_type?.text = "Sort by name"
        }
        else {
            logins.sortBy { it.date_modified }
            logins.reverse()
            logins_sort_type?.text = "Sort by modified date"
        }

        if (loginsAdapter == null) {
            loginsAdapter =
                LoginsRecyclerViewAdapter(
                    activity!!.baseContext,
                    logins,
                    activity!!.packageManager,
                    this
                )
        }

        loginsAdapter?.updateLogins(logins)

        login_cnt?.text = logins.size.toString()

        val vault = VaultRepository.getLastActiveVault()
        vault_name?.text = vault.name
        vault_view?.setBackgroundColor(resources.getColor(vault.getVaultColorResource()))
        vault_name?.setTextColor(resources.getColor(vault.getVaultColorAccent()))
        vault_icon?.setColorFilter(resources.getColor(vault.getVaultColorAccent()))

        no_items_view?.visibility = if (logins.size > 0) View.GONE else View.VISIBLE
        fab?.visibility = if (logins.size > 0) View.VISIBLE else View.GONE
        logins_counter_container?.visibility = if (logins.size > 0) View.VISIBLE else View.GONE
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
            updateUI(this.activeLogins)
            Toast.makeText(context, "Login successfully created", Toast.LENGTH_SHORT).show()
        } else if (resultCode == deleteLoginSuccess) {
            updateUI(this.activeLogins)
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
        AnalyticsRepository.trackEvent(AnalyticsRepository.LOGIN_PASS_COPY)
        Toast.makeText(context, login.name+" password copied", Toast.LENGTH_SHORT).show()
    }

    private fun animateItemsIn() {
        Timeline.createParallel()
            .push(Tween.to(fab, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(fab, Translation.XY).target(0f,-72.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            //.push(Tween.to(adview_container, Alpha.VIEW, 1.0f).target(1.0f))
            //.push(Tween.to(adview_container, Translation.XY).target(0f,-90.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(fab))
    }
     */
}
