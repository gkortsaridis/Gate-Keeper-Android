package gr.gkortsaridis.gatekeeper.UI.Vaults


import android.app.AlertDialog
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.github.florent37.shapeofview.shapes.RoundRectView
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.VaultColor
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.VaultCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultEditListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultInfoDismissListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import org.w3c.dom.Text

class VaultInfoFragment(private val vault: Vault, private val listener: VaultInfoDismissListener) : DialogFragment() {

    private lateinit var vaultName: EditText
    private lateinit var redColorContainer: RoundRectView
    private lateinit var greenColorContainer: RoundRectView
    private lateinit var blueColorContainer: RoundRectView
    private lateinit var yellowColorContainer: RoundRectView
    private lateinit var coralColorContainer: RoundRectView
    private lateinit var redColor: View
    private lateinit var greenColor: View
    private lateinit var blueColor: View
    private lateinit var yellowColor: View
    private lateinit var coralColor: View
    private lateinit var saveVault: Button
    private lateinit var deleteVault: Button
    private lateinit var dialog: ViewDialog
    private lateinit var actionTitle: TextView

    private lateinit var vaultColor: VaultColor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_vault_info, container, false)

        dialog = ViewDialog(activity!!)

        vaultName = view.findViewById(R.id.vault_name_et)
        redColorContainer = view.findViewById(R.id.red_color_container)
        redColor = view.findViewById(R.id.red_color)
        greenColorContainer = view.findViewById(R.id.green_color_container)
        greenColor = view.findViewById(R.id.green_color)
        blueColorContainer = view.findViewById(R.id.blue_color_container)
        blueColor = view.findViewById(R.id.blue_color)
        yellowColorContainer = view.findViewById(R.id.yellow_color_container)
        yellowColor = view.findViewById(R.id.yellow_color)
        coralColorContainer = view.findViewById(R.id.coral_color_container)
        coralColor = view.findViewById(R.id.coral_color)
        saveVault = view.findViewById(R.id.save_vault)
        deleteVault = view.findViewById(R.id.delete_vault)
        actionTitle = view.findViewById(R.id.action_title)

        if (vault.id == "-1") {
            actionTitle.text = "Create Vault"
            deleteVault.text = "Cancel"
            deleteVault.setTextColor(resources.getColor(R.color.cancel_grey))
        } else {
            actionTitle.text = "Edit Vault"
            deleteVault.text = "Delete"
            deleteVault.setTextColor(resources.getColor(R.color.error_red))
        }

        saveVault.setOnClickListener {
            if (vault.id == "-1"){
                createVault()
                deleteVault.visibility = View.GONE
            } else {
                saveVault()
                if (VaultRepository.allVaults.size > 1) {
                    deleteVault.visibility = View.VISIBLE
                } else {
                    deleteVault.visibility = View.GONE
                }
            }
        }
        deleteVault.setOnClickListener {
            if (vault.id != "-1") {
                displayVaultDeleteDialog(vault)
            } else {
                dismiss()
            }
        }

        redColor.setOnClickListener {
            vaultColor = VaultColor.Red
            updateColors()
        }
        greenColor.setOnClickListener {
            vaultColor = VaultColor.Green
            updateColors()
        }
        blueColor.setOnClickListener {
            vaultColor = VaultColor.Blue
            updateColors()
        }
        yellowColor.setOnClickListener {
            vaultColor = VaultColor.Yellow
            updateColors()
        }
        coralColor.setOnClickListener {
            vaultColor = VaultColor.Coral
            updateColors()
        }
        vaultName.setText(vault.name)
        vaultColor = vault.color ?: VaultColor.Blue
        updateColors()

        return view
    }

    private fun updateColors() {
        redColorContainer.setBorderColor(resources.getColor(R.color.underline_grey))
        greenColorContainer.setBorderColor(resources.getColor(R.color.underline_grey))
        blueColorContainer.setBorderColor(resources.getColor(R.color.underline_grey))
        yellowColorContainer.setBorderColor(resources.getColor(R.color.underline_grey))
        coralColorContainer.setBorderColor(resources.getColor(R.color.underline_grey))

        when (vaultColor) {
            VaultColor.Red -> {
                redColorContainer.setBorderColor(resources.getColor(R.color.mate_black))
            }
            VaultColor.Green -> {
                greenColorContainer.setBorderColor(resources.getColor(R.color.mate_black))
            }
            VaultColor.Blue -> {
                blueColorContainer.setBorderColor(resources.getColor(R.color.mate_black))
            }
            VaultColor.Yellow -> {
                yellowColorContainer.setBorderColor(resources.getColor(R.color.mate_black))
            }
            VaultColor.Coral -> {
                coralColorContainer.setBorderColor(resources.getColor(R.color.mate_black))
            }
        }

    }

    private fun createVault() {
        vault.name = vaultName.text.toString()
        vault.color = vaultColor
        dialog.showDialog()
        VaultRepository.createVault(vault, object : VaultCreateListener {
            override fun onVaultCreated(vault: Vault) {
                dialog.hideDialog()
                VaultRepository.addLocalVault(vault)
                //VaultRepository.allVaults.add(vault)
                listener.onVaultInfoFragmentDismissed()
                dismiss()
            }
        })
    }

    private fun displayVaultDeleteDialog(vault: Vault){
        if (VaultRepository.allVaults.size > 1) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete Vault")
            builder.setMessage("Are you sure you wish to delete this vault and its content?")

            builder.setNegativeButton("CANCEL") { dialog, _ -> dialog.cancel() }
            builder.setPositiveButton("DELETE") { _, _ ->
                dialog.showDialog()
                VaultRepository.deleteVault(vault, object: VaultEditListener{
                    override fun onVaultDeleted() {
                        super.onVaultDeleted()
                        dialog.hideDialog()
                        VaultRepository.removeLocalVault(vault)
                        //VaultRepository.allVaults.remove(vault)
                        listener.onVaultInfoFragmentDismissed()
                        dismiss()
                    }
                })
            }

            val dialog = builder.create()
            dialog.show()

            val positiveButton: Button = dialog.getButton(BUTTON_POSITIVE)
            positiveButton.setTextColor(resources.getColor(R.color.error_red))

            val negativeButton: Button = dialog.getButton(BUTTON_NEGATIVE)
            negativeButton.setTextColor(resources.getColor(R.color.mate_black))
        }else {
            Toast.makeText(context,"You cannot delete your only Vault", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveVault() {
        dialog.showDialog()
        VaultRepository.editVault(vaultName.text.toString(), vaultColor, vault, object: VaultEditListener{
            override fun onVaultEdited(vault: Vault) {
                dialog.hideDialog()
                VaultRepository.updateLocalVault(vault)
                //VaultRepository.allVaults.replaceAll { if(it.id == vault.id) vault else it }
                listener.onVaultInfoFragmentDismissed()
                dismiss()
            }
        })
    }
}
