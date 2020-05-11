package gr.gkortsaridis.gatekeeper.UI.Account

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.purchasePackageWith
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.InAppPurchasesListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.PlansRecyclerViewAdapter

class AccountStatusActivity : AppCompatActivity(), InAppPurchasesListener {

    private val TAG = "_GATEKEEPER_BILLING_"

    private lateinit var plansRecyclerView: RecyclerView
    private lateinit var plansAdapter: PlansRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_status)

        plansRecyclerView = findViewById(R.id.plans_recycler_view)
        plansAdapter = PlansRecyclerViewAdapter(this, arrayListOf(), this)
        plansRecyclerView.adapter = plansAdapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(plansRecyclerView)
        plansRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                Log.i("REVENUECAT ERROR", error.toString())
                Toast.makeText(baseContext, error.toString(), Toast.LENGTH_SHORT).show()
            },
            onSuccess = { offerings ->
                offerings.current?.availablePackages?.takeUnless { it.isNullOrEmpty() }?.let {
                    // Display packages for sale
                    Log.i("REVENUECAT SUCCESS", it.toString())
                    plansAdapter.updatePackages(ArrayList(it))
                }
            }
        )


    }

    override fun onSubscriptionBuyTouched(packageItem: Package) {
        Purchases.sharedInstance.purchasePackageWith(
            this,
            packageItem,
            onError = { error, userCancelled ->
                /* No purchase */
                Log.i("REVENUECAT PURCHASE ERROR", "$userCancelled $error")
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
            },
            onSuccess = { product, purchaserInfo ->
                Log.i("REVENUECAT PRODUCT", product.toString())
                Log.i("REVENUECAT PURCHASER INFO", purchaserInfo.toString())
                GateKeeperApplication.purchaserInfo = purchaserInfo
                if(AuthRepository.isPlusUser()) {
                    Toast.makeText(this, "YOU ARE A PRO USER", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "YOU ARE NOT A PRO USER", Toast.LENGTH_SHORT).show()
                }
        })
    }
}
