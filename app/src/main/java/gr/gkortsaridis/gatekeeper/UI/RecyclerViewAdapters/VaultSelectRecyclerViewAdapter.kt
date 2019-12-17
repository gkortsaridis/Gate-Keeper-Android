package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        private var editView: RelativeLayout? = null
        private var deleteView: RelativeLayout? = null
        private var view: LinearLayout? = null

        init {
            view = v.findViewById(R.id.vault_main_container)
            editView = v.findViewById(R.id.vault_edit_container)
            deleteView = v.findViewById(R.id.vault_delete_container)
            vaultName = v.findViewById(R.id.vault_name)

        }

        fun bindVault(vault: Vault, listener: VaultClickListener){
            view?.setOnClickListener{ listener.onVaultClicked(vault) }
            this.vaultName?.text = vault.name
        }

    }
}