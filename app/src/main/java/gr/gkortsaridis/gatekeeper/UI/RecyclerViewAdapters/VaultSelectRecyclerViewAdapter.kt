package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Interfaces.VaultClickListener
import gr.gkortsaridis.gatekeeper.R

class VaultSelectRecyclerViewAdapter(
    private val context: Context,
    private val vaults: ArrayList<Vault>,
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
        holder.bindVault(vaultItem, listener)
    }

    class VaultViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private var vaultName: TextView? = null
        private var view: View = v

        init {
            vaultName = view.findViewById(R.id.vault_name)
        }

        fun bindVault(vault: Vault, listener: VaultClickListener){
            view.setOnClickListener{ listener.onVaultClicked(vault) }
            this.vaultName?.text = vault.name
        }

    }
}