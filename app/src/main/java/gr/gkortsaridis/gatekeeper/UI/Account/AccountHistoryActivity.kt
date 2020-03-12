package gr.gkortsaridis.gatekeeper.UI.Account

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_account_history.*
import kotlinx.android.synthetic.main.fragment_account_log.*

class AccountHistoryActivity : AppCompatActivity() {

    private lateinit var viewDialog : ViewDialog

    private lateinit var accountLogFragment: AccountLogFragment
    private lateinit var accountDevicesFragment: AccountDevicesFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_history)

        viewDialog = ViewDialog(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        accountLogFragment = AccountLogFragment()
        accountDevicesFragment = AccountDevicesFragment()

        val adapter = AccountHistoryTabPagerAdapter(supportFragmentManager)
        adapter.addFragment(accountLogFragment, "History")
        adapter.addFragment(accountDevicesFragment, "Devices")

        viewPager.offscreenPageLimit = 3
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        retrieveLogs()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun retrieveLogs() {
        viewDialog.showDialog()
        val disposable = GateKeeperAPI.api.getUserLogs(AuthRepository.getUserID())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    viewDialog.hideDialog()
                    swipe_refresh.isRefreshing = false
                    if (it.data != null) {
                        GateKeeperApplication.userLog = it.data
                        accountLogFragment.updateLogs()
                        accountDevicesFragment.updateLogs()
                    } else {
                        GateKeeperApplication.userLog = ArrayList()
                        accountLogFragment.updateLogs()
                        accountDevicesFragment.updateLogs()
                    }
                },
                {
                    viewDialog.hideDialog()
                    GateKeeperApplication.userLog = ArrayList()
                    accountLogFragment.updateLogs()
                    accountDevicesFragment.updateLogs()
                }
            )

    }
}
