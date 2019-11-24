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