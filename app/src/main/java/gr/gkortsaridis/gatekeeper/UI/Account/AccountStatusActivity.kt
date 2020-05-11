package gr.gkortsaridis.gatekeeper.UI.Account

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import gr.gkortsaridis.gatekeeper.Interfaces.InAppPurchasesListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.PlansRecyclerViewAdapter

class AccountStatusActivity : AppCompatActivity() {

    private val TAG = "_GATEKEEPER_BILLING_"

    private lateinit var plansRecyclerView: RecyclerView
    private lateinit var plansAdapter: PlansRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_status)

        plansRecyclerView = findViewById(R.id.plans_recycler_view)
        plansAdapter = PlansRecyclerViewAdapter(this, arrayListOf(), null)
        plansRecyclerView.adapter = plansAdapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(plansRecyclerView)
        plansRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    }
}
