package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.VaultColor
import gr.gkortsaridis.gatekeeper.Interfaces.VaultClickListener
import gr.gkortsaridis.gatekeeper.R

class VaultSelectRecyclerViewAdapter(
    private val context: Context,
    private val vaults: ArrayList<Vault>,
    private val active_vault: String?,
    private val hideExtras: Boolean,
    private val listener: VaultClickListener): RecyclerView.Adapter<VaultSelectRecyclerViewAdapter.VaultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaultViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_vault, parent, false)
        return VaultViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return vaults.size
    }

    override fun onBindViewHolder(holder: VaultViewHolder, position: Int) {
        val vaultItem = vaults[position]
        holder.bindVault(context, vaultItem, active_vault, hideExtras, listener)
    }

    class VaultViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private var vaultName: TextView? = null
        private var dots: ImageButton? = null
        private var view: LinearLayout? = null
        private var vaultIcon: ImageView? = null
        private var vaultInitialContainer: RelativeLayout? = null

        init {
            view = v.findViewById(R.id.vault_main_container)
            dots = v.findViewById(R.id.dots)
            vaultName = v.findViewById(R.id.vault_name)
            vaultIcon = v.findViewById(R.id.vault_icon)
            vaultInitialContainer = v.findViewById(R.id.vault_background)
        }

        fun bindVault(context: Context, vault: Vault, activeVault: String?, hideExtras: Boolean, listener: VaultClickListener){

            when (vault.color) {
                VaultColor.Red -> {
                    vaultInitialContainer?.setBackgroundResource(R.color.vault_red_1)
                    vaultIcon?.setColorFilter(ContextCompat.getColor(context, R.color.vault_white_2), android.graphics.PorterDuff.Mode.SRC_ATOP)
                    dots?.backgroundTintList = ContextCompat.getColorStateList(context, R.color.vault_white_2)
                    vaultName?.setTextColor(context.resources.getColor(R.color.vault_white_2))
                }
                VaultColor.Green -> {
                    vaultInitialContainer?.setBackgroundResource(R.color.vault_green_1)
                    vaultIcon?.setColorFilter(ContextCompat.getColor(context, R.color.vault_white_2), android.graphics.PorterDuff.Mode.SRC_ATOP)
                    dots?.backgroundTintList = ContextCompat.getColorStateList(context, R.color.vault_white_2)
                    vaultName?.setTextColor(context.resources.getColor(R.color.vault_white_2))
                }
                VaultColor.Blue -> {
                    vaultInitialContainer?.setBackgroundResource(R.color.vault_blue_1)
                    vaultIcon?.setColorFilter(ContextCompat.getColor(context, R.color.vault_white_2), android.graphics.PorterDuff.Mode.SRC_ATOP)
                    dots?.backgroundTintList = ContextCompat.getColorStateList(context, R.color.vault_white_2)
                    vaultName?.setTextColor(context.resources.getColor(R.color.vault_white_2))
                }
                VaultColor.Yellow -> {
                    vaultInitialContainer?.setBackgroundResource(R.color.vault_yellow_1)
                    vaultIcon?.setColorFilter(ContextCompat.getColor(context, R.color.mate_black), android.graphics.PorterDuff.Mode.SRC_ATOP)
                    dots?.backgroundTintList = ContextCompat.getColorStateList(context, R.color.mate_black)
                    vaultName?.setTextColor(context.resources.getColor(R.color.mate_black))
                }
                VaultColor.White -> {
                    vaultInitialContainer?.setBackgroundResource(R.color.vault_white_2)
                    vaultIcon?.setColorFilter(ContextCompat.getColor(context, R.color.mate_black), android.graphics.PorterDuff.Mode.SRC_ATOP)
                    dots?.backgroundTintList = ContextCompat.getColorStateList(context, R.color.mate_black)
                    vaultName?.setTextColor(context.resources.getColor(R.color.mate_black))
                }
            }


            view?.setOnClickListener{ listener.onVaultClicked(vault) }
            this.vaultName?.text = vault.name
            if (vault.id == activeVault) { this.vaultName?.typeface = Typeface.DEFAULT_BOLD }

            dots?.visibility = if (hideExtras || vault.id == "-1") View.GONE else View.VISIBLE
            dots?.setOnClickListener { listener.onVaultOptionsClicker(vault) }
        }

    }
}