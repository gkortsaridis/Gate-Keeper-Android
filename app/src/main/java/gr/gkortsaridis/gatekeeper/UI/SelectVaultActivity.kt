package gr.gkortsaridis.gatekeeper.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.VaultClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.VaultSelectRecyclerViewAdapter

class SelectVaultActivity : AppCompatActivity(), VaultClickListener {

    private lateinit var vaultsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_vault)

        vaultsRecyclerView = findViewById(R.id.vault_recycler_view)
        vaultsRecyclerView.layoutManager = LinearLayoutManager(this)
        vaultsRecyclerView.adapter = VaultSelectRecyclerViewAdapter(this, GateKeeperApplication.vaults, this)
    }

    override fun onVaultClicked(vault: Vault) {
        GateKeeperApplication.activeVault = vault
        finish()
    }
}
