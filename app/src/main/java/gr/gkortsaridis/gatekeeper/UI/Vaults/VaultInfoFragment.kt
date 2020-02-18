package gr.gkortsaridis.gatekeeper.UI.Vaults


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.github.florent37.shapeofview.shapes.RoundRectView
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.VaultColor
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.VaultEditListener
import gr.gkortsaridis.gatekeeper.Interfaces.VaultInfoDismissListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository

class VaultInfoFragment(private val vault: Vault, private val listener: VaultInfoDismissListener) : DialogFragment(), VaultEditListener {

    private lateinit var vaultName: EditText
    private lateinit var redColorContainer: RoundRectView
    private lateinit var greenColorContainer: RoundRectView
    private lateinit var blueColorContainer: RoundRectView
    private lateinit var yellowColorContainer: RoundRectView
    private lateinit var redColor: View
    private lateinit var greenColor: View
    private lateinit var blueColor: View
    private lateinit var yellowColor: View
    private lateinit var saveVault: Button
    private lateinit var vaultBackground: View

    private lateinit var vaultColor: VaultColor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_vault_info, container, false)

        vaultName = view.findViewById(R.id.vault_name_et)
        redColorContainer = view.findViewById(R.id.red_color_container)
        redColor = view.findViewById(R.id.red_color)
        greenColorContainer = view.findViewById(R.id.green_color_container)
        greenColor = view.findViewById(R.id.green_color)
        blueColorContainer = view.findViewById(R.id.blue_color_container)
        blueColor = view.findViewById(R.id.blue_color)
        yellowColorContainer = view.findViewById(R.id.yellow_color_container)
        yellowColor = view.findViewById(R.id.yellow_color)
        saveVault = view.findViewById(R.id.save_vault)
        vaultBackground = view.findViewById(R.id.vault_background)

        saveVault.setOnClickListener { saveVault() }
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

        vaultName.setText(vault.name)
        vaultColor = vault.color ?: VaultColor.White
        updateColors()

        return view
    }

    private fun updateColors() {
        redColorContainer.setBorderColor(resources.getColor(android.R.color.transparent))
        greenColorContainer.setBorderColor(resources.getColor(android.R.color.transparent))
        blueColorContainer.setBorderColor(resources.getColor(android.R.color.transparent))
        yellowColorContainer.setBorderColor(resources.getColor(android.R.color.transparent))


        when (vaultColor) {
            VaultColor.Red -> {
                redColorContainer.setBorderColor(resources.getColor(R.color.mate_black))
                vaultBackground.setBackgroundResource(R.drawable.vault_color_red)
            }
            VaultColor.Green -> {
                greenColorContainer.setBorderColor(resources.getColor(R.color.mate_black))
                vaultBackground.setBackgroundResource(R.drawable.vault_color_green)

            }
            VaultColor.Blue -> {
                blueColorContainer.setBorderColor(resources.getColor(R.color.mate_black))
                vaultBackground.setBackgroundResource(R.drawable.vault_color_blue)
            }
            VaultColor.Yellow -> {
                yellowColorContainer.setBorderColor(resources.getColor(R.color.mate_black))
                vaultBackground.setBackgroundResource(R.drawable.vault_color_yellow)
            }
        }

    }

    private fun saveVault() {
        VaultRepository.editVault(vaultName.text.toString(), vaultColor, vault, this)
    }

    override fun onVaultEdited(vault: Vault) {
        GateKeeperApplication.vaults.replaceAll { if(it.id == vault.id) vault else it }
        listener.onVaultInfoFragmentDismissed()
        dismiss()
    }

    override fun onVaultDeleted() {
    }

}
