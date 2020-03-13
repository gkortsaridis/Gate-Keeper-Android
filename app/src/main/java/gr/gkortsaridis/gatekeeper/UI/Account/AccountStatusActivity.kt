package gr.gkortsaridis.gatekeeper.UI.Account

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import gr.gkortsaridis.gatekeeper.Interfaces.InAppPurchasesListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.PlansRecyclerViewAdapter

class AccountStatusActivity : AppCompatActivity(), PurchasesUpdatedListener, InAppPurchasesListener {

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
        plansAdapter = PlansRecyclerViewAdapter(this, arrayListOf(), this)
        plansRecyclerView.adapter = plansAdapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(plansRecyclerView)
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

                    val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
                    val billingResult = purchasesResult.billingResult
                    val purchasesList = purchasesResult.purchasesList
                    purchasesList.forEach {
                        Log.i(TAG, it.toString())
                        Log.i(TAG,"--> PURCHASE STATE: " + if (it.purchaseState == Purchase.PurchaseState.PURCHASED) "PURCHASED" else if (it.purchaseState == Purchase.PurchaseState.PENDING) "PENDING" else "UNSPECIFIED")
                    }
                } else {
                    Log.i(TAG,"Billing Setup Error Code: "+p0.toString())
                }
            }
        })
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

    override fun onSubscriptionBuyTouched(skuDetails: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        var billingResult = billingClient.launchBillingFlow(this, flowParams)
        Log.i("BillingResult", billingResult.toString())
    }
}
