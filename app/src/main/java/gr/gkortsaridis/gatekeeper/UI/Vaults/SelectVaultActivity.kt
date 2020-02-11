package gr.gkortsaridis.gatekeeper.UI.Vaults

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
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
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.VaultClickListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultEditListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultRetrieveListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.DeviceRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.VaultSelectRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import gr.gkortsaridis.gatekeeper.Utils.dp


class SelectVaultActivity : AppCompatActivity(), VaultClickListener, VaultEditListener {

    private lateinit var vaultsRecyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var addVaultFab: FloatingActionButton
    private var vaultId: String? = null
    private val viewDialog = ViewDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_vault)

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

    override fun onVaultDeleted() {
        viewDialog.hideDialog()
        updateVaultsRecyclerView()
    }

    override fun onVaultRenamed() {
        viewDialog.hideDialog()
        updateVaultsRecyclerView()
    }

    override fun onVaultOptionsClicker(vault: Vault) {
        val builder = AlertDialog.Builder(this)
        val parent = RelativeLayout(this)
        parent.layoutParams = ViewGroup.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        parent.setPadding(16.dp,16.dp,16.dp,16.dp)
        val input = EditText(this)
        input.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(vault.name)
        parent.addView(input)

        builder.setTitle(vault.name)
        builder.setMessage("Vault Details")
        builder.setView(parent)

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.setNeutralButton("Delete") { dialog, _ ->
            dialog.cancel()
            displayVaultDeleteDialog(vault)
        }
        builder.setPositiveButton("SET NAME") { _, _ ->
            viewDialog.showDialog()
            VaultRepository.renameVault(input.text.toString(), vault, this)
        }

        builder.show()

    }

    private fun displayVaultDeleteDialog(vault: Vault){
        if (GateKeeperApplication.vaults.size > 1) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Vault")
            builder.setMessage("Are you sure you wish to delete this vault and its content?")

            builder.setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            builder.setPositiveButton("Yes") { _, _ ->
                viewDialog.showDialog()
                VaultRepository.deleteVault(vault, this)
            }

            builder.show()
        }else {
            Toast.makeText(this,"You cannot delete your only Vault", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateVaultsRecyclerView() {
        val action = intent.getStringExtra("action")
        val sortedVaults = ArrayList(GateKeeperApplication.vaults.sortedWith(compareBy {it.name}))
        if (action == GateKeeperConstants.ACTION_CHANGE_ACTIVE_VAULT && sortedVaults.size > 1) { sortedVaults.add(0, Vault("-1", AuthRepository.getUserID(), "All Vaults") )}
        vaultsRecyclerView.adapter = VaultSelectRecyclerViewAdapter(this, sortedVaults, vaultId, this)
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
