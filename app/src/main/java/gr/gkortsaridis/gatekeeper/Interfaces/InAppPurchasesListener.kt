package gr.gkortsaridis.gatekeeper.Interfaces

import com.android.billingclient.api.SkuDetails

interface InAppPurchasesListener {
    fun onSubscriptionBuyTouched(skuDetails: SkuDetails)
}