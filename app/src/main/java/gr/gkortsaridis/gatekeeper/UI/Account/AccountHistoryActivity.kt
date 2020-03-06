package gr.gkortsaridis.gatekeeper.UI.Account

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gr.gkortsaridis.gatekeeper.R
import kotlinx.android.synthetic.main.activity_account_history.*

class AccountHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_history)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val adapter = AccountHistoryTabPagerAdapter(supportFragmentManager)
        adapter.addFragment(AccountLogFragment(), "History")
        adapter.addFragment(AccountDevicesFragment(), "Devices")

        viewPager.offscreenPageLimit = 3
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
    }
}
