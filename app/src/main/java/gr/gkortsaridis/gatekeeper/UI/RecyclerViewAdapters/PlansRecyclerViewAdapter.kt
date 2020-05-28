package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.SkuDetails
import com.revenuecat.purchases.Package
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.VaultColor
import gr.gkortsaridis.gatekeeper.Interfaces.InAppPurchasesListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultClickListener
import gr.gkortsaridis.gatekeeper.R
import kotlin.math.roundToInt

class PlansRecyclerViewAdapter(
    private val context: Context,
    private var packages: ArrayList<Package>,
    private val listener: InAppPurchasesListener?): RecyclerView.Adapter<PlansRecyclerViewAdapter.VaultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaultViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_plan, parent, false)
        return VaultViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return packages.size
    }

    override fun onBindViewHolder(holder: VaultViewHolder, position: Int) {
        val packageItem = packages[position]
        holder.bindSku(context, packageItem, listener)
    }

    fun updatePackages(packages: ArrayList<Package>) {
        this.packages = packages
        notifyDataSetChanged()
    }

    class VaultViewHolder(v: View): RecyclerView.ViewHolder(v) {

        //private var mainContainer: View? = null
        private var title: TextView? = null
        private var description: TextView? = null
        private var price: TextView? = null
        private var currency: TextView? = null
        private var buyNow: RelativeLayout? = null
        private var perMonth: TextView? = null
        private var planName: TextView? = null

        init {
            //mainContainer = v.findViewById(R.id.background_color)
            description = v.findViewById(R.id.perks)
            price = v.findViewById(R.id.price_number)
            currency = v.findViewById(R.id.price_currency)
            title = v.findViewById(R.id.plan_title)
            buyNow = v.findViewById(R.id.buy_now)
            perMonth = v.findViewById(R.id.per_month)
            planName = v.findViewById(R.id.plan_name)
        }

        fun bindSku(context: Context, packageItem: Package, listener: InAppPurchasesListener?){
            packageItem.product.price
            when {
                packageItem.product.price == "Free" -> {
                    planName?.text = "GateKeeper\nBasic"
                    title?.text = packageItem.product.title
                    price?.text = packageItem.product.price
                    currency?.text = ""
                    description?.text = context.getString(R.string.free_plan_description)
                    perMonth?.visibility = View.GONE
                }
                packageItem.product.sku == "gate_keeper_plus_yearly" -> {
                    planName?.text = "GateKeeper\nPlus+"
                    val priceNumber = packageItem.product.price.substring(1)
                    val priceCurrency = packageItem.product.price[0].toString()
                    val priceRounded = ((priceNumber.toFloat() / 12) * 100.0).roundToInt() / 100.0
                    price?.text = priceRounded.toString().replace(".0","")
                    currency?.text = priceCurrency
                    description?.text = "Charged "+packageItem.product.price+" yearly"+context.getString(R.string.yearly_plan_description)
                    title?.text = "Yearly Subscription"

                }
                packageItem.product.sku == "gatekeeper_plus_monthly" -> {
                    planName?.text = "GateKeeper\nPlus+"
                    val priceNumber = packageItem.product.price.substring(1)
                    val priceCurrency = packageItem.product.price[0].toString()
                    price?.text = priceNumber
                    currency?.text = priceCurrency
                    description?.text = context.getString(R.string.monthly_plan_description)
                    title?.text = "Monthly Subscription"
                }
                else -> {
                    //Just in case :)
                    val priceNumber = packageItem.product.price.substring(1)
                    val priceCurrency = packageItem.product.price[0].toString()
                    price?.text = priceNumber
                    currency?.text = priceCurrency
                    description?.text = packageItem.product.description
                    title?.text = packageItem.product.title
                }
            }

            buyNow?.setOnClickListener { listener?.onSubscriptionBuyTouched(packageItem) }
        }

    }
}