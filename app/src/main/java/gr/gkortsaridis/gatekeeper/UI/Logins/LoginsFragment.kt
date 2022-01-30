package gr.gkortsaridis.gatekeeper.UI.Logins


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.ads.AdRequest
import dagger.hilt.android.AndroidEntryPoint
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.VaultColor
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AnalyticsRepository
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository.LOGIN_SORT_TYPE_NAME
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.Composables.GateKeeperVaultSelector.vaultSelector
import gr.gkortsaridis.gatekeeper.Utils.*
import gr.gkortsaridis.gatekeeper.ViewModels.MainViewModel
import kotlinx.android.synthetic.main.fragment_logins.*
import kotlin.math.log

@AndroidEntryPoint
class LoginsFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val currentVault = viewModel.getLastActiveVault()
        val allLogins = viewModel.allLogins
        val allLoginsLive = viewModel.getAllLoginsLive(this)
        val loginAction = DataRepository.loginClickAction
        val sortType = DataRepository.loginSortType

        return ComposeView(requireContext()).apply {
            setContent { loginsPage(
                currentVault = currentVault,
                logins = allLoginsLive,
                sortType = sortType,
                loginAction = loginAction
            ) }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val adRequest = AdRequest.Builder().build()
        //adview.loadAd(adRequest)

        /*add_login_btn.setOnClickListener { startActivityForResult(Intent(activity, CreateLoginActivity::class.java), createLoginRequestCode) }
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

        adview_container.visibility = View.GONE*/
    }

    @Preview
    @Composable
    fun loginsPage(
        currentVault: Vault = GateKeeperDevelopMockData.mockVault,
        logins: LiveData<ArrayList<Login>> = GateKeeperDevelopMockData.mockLoginsLive,
        sortType: Int = LOGIN_SORT_TYPE_NAME,
        loginAction: Int = LoginsRepository.LOGIN_CLICK_ACTION_OPEN
    ) {
        val loginsLive = logins.observeAsState()

        val currentVaultLogins = MainViewModel.filterLoginsByVault(logins = loginsLive.value ?: ArrayList(), vault = currentVault)
        if (sortType == LOGIN_SORT_TYPE_NAME) {
            currentVaultLogins.sortBy { it.name.lowercase() }
        } else {
            currentVaultLogins.sortBy { it.date_modified }
            currentVaultLogins.reverse()
        }


        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(GateKeeperTheme.light_grey)
        ) {
            vaultSelector(currentVault = currentVault)
            if(currentVaultLogins.isNotEmpty()) {
                itemsCount(sortType = sortType, logins = currentVaultLogins)
                Box(modifier = Modifier.fillMaxSize()){
                    itemsList(logins = currentVaultLogins, loginAction = loginAction)
                    FloatingActionButton(
                        onClick = {
                            startActivity(Intent(requireActivity(), CreateLoginActivity::class.java))
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(24.dp)
                            .size(56.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = "",
                            modifier = Modifier.padding(all=12.dp)
                        )
                    }
                }

            } else {
                noLogins()
            }
        }

    }

    @Composable
    fun noLogins() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.padlock_grey),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp, 40.dp)
                        .padding(4.dp)
                )
                Text(
                    text = stringResource(id = R.string.no_logins_title),
                    modifier = Modifier.padding(top=8.dp),
                    fontWeight = FontWeight.Bold,
                    color = GateKeeperTheme.tone_black
                )
                Text(
                    text = stringResource(id = R.string.no_logins_message),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top=8.dp),
                    color = GateKeeperTheme.tone_black
                )
            }

            FloatingActionButton(
                onClick = {
                          startActivity(Intent(requireActivity(), CreateLoginActivity::class.java))
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .size(56.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "",
                    modifier = Modifier.padding(all=12.dp)
                )
            }
        }

    }

    @Composable
    fun itemsCount(logins: ArrayList<Login>, sortType: Int){
        Row(
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${logins.size}",
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
                text = if(sortType == LOGIN_SORT_TYPE_NAME) "Sort by name" else "Sort by modified date",
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
    fun itemsList(
        logins: ArrayList<Login>,
        loginAction: Int
    ){
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 32.dp, end= 32.dp, top = 8.dp, bottom = 60.dp)
        ) {
            items(logins) { login -> loginItem(login = login, loginAction = loginAction)}
        }
    }

    @Composable
    fun loginItem(
        login: Login,
        loginAction: Int
    ) {

        val vault = viewModel.getVaultById(login.vault_id)
        var color = "ffffff"
        when (vault?.color) {
            VaultColor.Red -> { color = "e53935" }
            VaultColor.Green -> { color = "4caf50" }
            VaultColor.Blue -> { color = "1e88e5" }
            VaultColor.Yellow -> { color = "fdd835" }
            VaultColor.White -> { color = "ffffff" }
        }

        val app = MainViewModel.getApplicationInfoByPackageName(login.url, requireActivity().packageManager)
        val appIcon = app?.loadIcon(requireActivity().packageManager)

        /*
        if (appIcon != null) {
            this.loginInitial?.visibility = View.GONE
            this.loginImage?.visibility = View.VISIBLE
            this.loginImage?.setImageDrawable(appIcon)
        } else {
            this.loginImage?.visibility = View.VISIBLE
            GlideApp.with(context)
                .load(FavIconDownloader.buildUrl(login.url, color))
                .placeholder(R.drawable.padlock)
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        val bitmap = resource?.toBitmap(8.dp, 8.dp)!!
                        return false
                    }
                })
                .into(loginImage!!)
        }*/

        Card(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(80.dp)
                .clickable { onLoginClicked(login) },
            elevation = 4.dp,
            shape = RoundedCornerShape(10.dp),
            backgroundColor = GateKeeperTheme.white
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

                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                ) {

                    Text(
                        text = login.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )


                    Text(
                        text = login.username,
                        fontSize = 18.sp
                    )

                }

                if (loginAction == LoginsRepository.LOGIN_CLICK_ACTION_OPEN) {
                    Image(
                        painter = painterResource(id = R.drawable.copy),
                        contentDescription = "Copy Password",
                        modifier = Modifier
                            .padding(start = 8.dp, end = 16.dp)
                            .size(24.dp, 24.dp)
                            .clickable { onLoginActionClicked(login) },
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.writing),
                        contentDescription = "Edit Login",
                        modifier = Modifier
                            .padding(start = 8.dp, end = 16.dp)
                            .size(24.dp, 24.dp)
                            .clickable { onLoginActionClicked(login) },
                    )
                }



            }
        }
    }

    private fun onLoginClicked(login: Login) {
        val action = DataRepository.loginClickAction
        if (action == LoginsRepository.LOGIN_CLICK_ACTION_OPEN) { openLogin(login) }
        else { copyLoginPassword(login) }
    }

    private fun onLoginActionClicked(login: Login) {
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
            requireContext(),
            ClipboardManager::class.java
        ) as ClipboardManager
        val clip = ClipData.newPlainText("label",login.password)
        clipboard.setPrimaryClip(clip)
        AnalyticsRepository.trackEvent(AnalyticsRepository.LOGIN_PASS_COPY)
        Toast.makeText(context, login.name+" password copied", Toast.LENGTH_SHORT).show()
    }

    /*
    private fun updateUI(allLogins: ArrayList<Login>) {
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
