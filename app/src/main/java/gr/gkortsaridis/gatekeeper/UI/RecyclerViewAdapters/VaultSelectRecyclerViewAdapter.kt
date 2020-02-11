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
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Interfaces.VaultClickListener
import gr.gkortsaridis.gatekeeper.R

class VaultSelectRecyclerViewAdapter(
    private val context: Context,
    private val vaults: ArrayList<Vault>,
    private val active_vault: String?,
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
        holder.bindVault(vaultItem, active_vault, listener)
    }

    class VaultViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private var vaultName: TextView? = null
        private var dots: ImageButton? = null
        private var view: LinearLayout? = null

        init {
            view = v.findViewById(R.id.vault_main_container)
            dots = v.findViewById(R.id.dots)
            vaultName = v.findViewById(R.id.vault_name)
        }

        fun bindVault(vault: Vault, activeVault: String?, listener: VaultClickListener){
            if (vault.id == "-1") { dots?.visibility = View.GONE }

            view?.setOnClickListener{ listener.onVaultClicked(vault) }
            this.vaultName?.text = vault.name
            if (vault.id == activeVault) {
                this.vaultName?.typeface = Typeface.DEFAULT_BOLD
            }

            dots?.setOnClickListener { listener.onVaultOptionsClicker(vault) }
        }

    }
}