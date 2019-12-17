package gr.gkortsaridis.gatekeeper.UI.Vaults

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.VaultClickListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultRetrieveListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.VaultSelectRecyclerViewAdapter


class SelectVaultActivity : AppCompatActivity(), VaultClickListener {

    private lateinit var vaultsRecyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var addVaultFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_vault)

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

        addVaultFab.setOnClickListener { createVault() }
    }

    override fun onVaultClicked(vault: Vault) {
        VaultRepository.setActiveVault(vault)
        finish()
    }

    private fun updateVaultsRecyclerView() {
        vaultsRecyclerView.adapter = VaultSelectRecyclerViewAdapter(this, GateKeeperApplication.vaults, this)
    }

    private fun createVault() {
        val viewDialog = ViewDialog(this)
        val builder = AlertDialog.Builder(this)
        val parent = RelativeLayout(this)
        parent.layoutParams = ViewGroup.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        parent.setPadding(50,50,50,50)
        val input = EditText(this)
        input.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        input.inputType = InputType.TYPE_CLASS_TEXT
        parent.addView(input)

        builder.setTitle("Create Vault")
        builder.setMessage("Set a name for your new vault")
        builder.setView(parent)

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.setPositiveButton("OK") { _, _ ->
            viewDialog.showDialog()
            VaultRepository.createVault(input.text.toString(),object : VaultCreateListener{
                override fun onVaultCreated() {
                    VaultRepository.retrieveVaultsByAccountID(AuthRepository.getUserID(),object: VaultRetrieveListener{
                        override fun onVaultsRetrieveSuccess(vaults: ArrayList<Vault>) {
                            GateKeeperApplication.vaults = vaults
                            viewDialog.hideDialog()
                            updateVaultsRecyclerView()
                        }

                        override fun onVaultsRetrieveError(e: Exception) { viewDialog.hideDialog() }
                    })
                }

                override fun onVaultCreateError() {}
            })
        }

        builder.show()
    }
}
