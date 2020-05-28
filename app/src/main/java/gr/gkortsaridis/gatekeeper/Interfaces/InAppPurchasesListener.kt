package gr.gkortsaridis.gatekeeper.Interfaces

import com.revenuecat.purchases.Package

interface InAppPurchasesListener {
    fun onSubscriptionBuyTouched(packageItem: Package)
}