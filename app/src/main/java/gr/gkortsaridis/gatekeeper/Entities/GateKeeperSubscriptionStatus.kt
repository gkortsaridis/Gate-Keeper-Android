package gr.gkortsaridis.gatekeeper.Entities

data class GateKeeperSubscriptionStatus(var subscription_id: String, var purchaseTime: Long, var purchaseToken: String) {

    fun subscriptionEnd(): Long {
        return -1
    }
}