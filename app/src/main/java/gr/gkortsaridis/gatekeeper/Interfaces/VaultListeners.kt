package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.Vault

interface VaultCreateListener {
    fun onVaultCreated(vault: Vault)
    fun onVaultCreateError(errorCode: Int, errorMsg: String){}
}

interface VaultSetupListener {
    fun onVaultSetupComplete()
    fun onVaultSetupError(errorCode: Int, errorMsg: String)
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
    fun onVaultEdited(vault: Vault){}
    fun onVaultEditError(errorCode: Int, errorMsg: String){}
    fun onVaultDeleted(){}
    fun onVaultDeleteError(errorCode: Int, errorMsg: String){}
}

interface VaultInfoDismissListener {
    fun onVaultInfoFragmentDismissed()
}
