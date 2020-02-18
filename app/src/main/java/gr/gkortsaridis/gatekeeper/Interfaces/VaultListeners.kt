package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.Vault

interface VaultCreateListener {
    fun onVaultCreated()
    fun onVaultCreateError()
}

interface VaultSetupListener {
    fun onVaultSetupComplete()
    fun onVaultSetupError()
}

interface VaultRetrieveListener {
    fun onVaultsRetrieveSuccess(vaults: ArrayList<Vault>)
    fun onVaultsRetrieveError(e: Exception)
}

interface VaultClickListener {
    fun onVaultClicked(vault: Vault)
    fun onVaultEditClicked(vault: Vault){}
    fun onVaultDeleteClicked(vault: Vault){}
    fun onVaultOptionsClicker(vault: Vault)
}

interface VaultEditListener {
    fun onVaultEdited(vault: Vault)
    fun onVaultDeleted(){}
}

interface VaultInfoDismissListener {
    fun onVaultInfoFragmentDismissed()
}
