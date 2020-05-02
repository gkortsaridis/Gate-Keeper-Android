package gr.gkortsaridis.gatekeeper.UI.Vaults

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.VaultColor
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.*
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.VaultSelectRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants


class SelectVaultActivity : AppCompatActivity(), VaultClickListener, VaultEditListener, VaultInfoDismissListener {

    private lateinit var vaultsRecyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var addVaultFab: FloatingActionButton
    private var vaultId: String? = null
    private val viewDialog = ViewDialog(this)
    private var action: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_vault)
        action = intent.getStringExtra("action")
        vaultId = intent.getStringExtra("vault_id")

        addVaultFab = findViewById(R.id.add_vault)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.title = "Select Vault"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        vaultsRecyclerView = findViewById(R.id.vault_recycler_view)
        vaultsRecyclerView.layoutManager = LinearLayoutManager(this)
        updateVaultsRecyclerView()

        addVaultFab.visibility = if(action == GateKeeperConstants.ACTION_CHANGE_VAULT) View.GONE else View.VISIBLE

        addVaultFab.setOnClickListener { createVault() }
    }

    override fun onVaultClicked(vault: Vault) {
        val action = intent.getStringExtra("action")
        if (action == GateKeeperConstants.ACTION_CHANGE_VAULT) {
            val intent = Intent()
            intent.data = Uri.parse(vault.id)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }else if (action == GateKeeperConstants.ACTION_CHANGE_ACTIVE_VAULT) {
            VaultRepository.setActiveVault(vault)
            finish()
        }

    }

    override fun onVaultEdited(vault: Vault) {
        viewDialog.hideDialog()
        updateVaultsRecyclerView()
    }

    override fun onVaultDeleted() {
        viewDialog.hideDialog()
        updateVaultsRecyclerView()
    }

    override fun onVaultOptionsClicker(vault: Vault) {
        val vaultInfoFragment = VaultInfoFragment(vault, this)
        vaultInfoFragment.show(supportFragmentManager,null)
    }

    private fun updateVaultsRecyclerView() {
        val sortedVaults = ArrayList(GateKeeperApplication.vaults.sortedWith(compareBy {it.name}))
        if (action == GateKeeperConstants.ACTION_CHANGE_ACTIVE_VAULT && sortedVaults.size > 1) {
            sortedVaults.add(0, VaultRepository.allVaults )
        }
        vaultsRecyclerView.adapter = VaultSelectRecyclerViewAdapter(
            this,
            sortedVaults,
            vaultId,
            action == GateKeeperConstants.ACTION_CHANGE_VAULT,
            this)
    }

    private fun createVault() {
        val newVault = Vault(id = "-1", account_id = AuthRepository.getUserID(), name = "", color = VaultColor.Blue)
        val vaultInfoFragment = VaultInfoFragment(newVault, this)
        vaultInfoFragment.show(supportFragmentManager,null)
    }

    override fun onVaultInfoFragmentDismissed() {
        updateVaultsRecyclerView()
    }
}
