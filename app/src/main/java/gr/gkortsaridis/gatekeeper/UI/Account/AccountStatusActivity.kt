package gr.gkortsaridis.gatekeeper.UI.Account

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.PlansRecyclerViewAdapter

class AccountStatusActivity : AppCompatActivity(), PurchasesUpdatedListener {

    private val TAG = "_GATEKEEPER_BILLING_"

    private lateinit var billingClient: BillingClient
    private lateinit var plansRecyclerView: RecyclerView
    private lateinit var plansAdapter: PlansRecyclerViewAdapter
    private lateinit var skus: ArrayList<SkuDetails>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_status)

        skus = ArrayList()
        skus.add(SkuDetails("{'title':'', 'description': '', 'productId': '-1', 'type': 'free', 'price':'Free', 'price_amount_micros':0, 'price_currency_code':''}"))

        plansRecyclerView = findViewById(R.id.plans_recycler_view)
        plansAdapter = PlansRecyclerViewAdapter(this, arrayListOf(), null)
        plansRecyclerView.adapter = plansAdapter
        plansRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient
            .newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                println("BILLING | onBillingServiceDisconnected | DISCONNECTED")
            }

            override fun onBillingSetupFinished(p0: BillingResult?) {
                if (p0?.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG,"Billing Setup OK")
                    //loadInAppProducts()
                    loadSubscriptionProducts()
                } else {
                    Log.i(TAG,"Billing Setup Error Code: "+p0.toString())
                }
            }
        })
    }

    private fun loadInAppProducts() {
        val skuList = listOf("gatekeeper_extra_space")
        if (billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build()
            billingClient.querySkuDetailsAsync(params) { result, skuDetailsList ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG ,"InApp Products retrieve OK")
                    Log.i(TAG ,"Size: ${skuDetailsList.size}")
                    skuDetailsList.forEach {
                        Log.i("$TAG ->",it.toString())
                    }
                } else {
                    Log.i(TAG,"InApp Products retrieve error code: ${result.responseCode}")
                }
            }

        } else {
            println("Billing Client not ready")
        }
    }

    private fun loadSubscriptionProducts() {
        val skuList = listOf("gatekeeper_plus_monthly", "gate_keeper_plus_yearly")
        if (billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build()
            billingClient.querySkuDetailsAsync(params) { result, skuDetailsList ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG ,"InApp Products retrieve OK")
                    Log.i(TAG ,"Size: ${skuDetailsList.size}")
                    skus.addAll(ArrayList(skuDetailsList))
                    plansAdapter.updateSkus(skus)
                    skuDetailsList.forEach { Log.i("$TAG ->",it.toString()) }
                } else {
                    Log.i(TAG,"InApp Products retrieve error code: ${result.responseCode}")
                }
            }
        } else {
            println("Billing Client not ready")
        }
    }

    override fun onPurchasesUpdated(p0: BillingResult?, p1: MutableList<Purchase>?) {
        Log.i(TAG, p0.toString())
    }
}
