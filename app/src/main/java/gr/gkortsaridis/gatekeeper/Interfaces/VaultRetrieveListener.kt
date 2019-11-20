package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.Vault

interface VaultRetrieveListener {
    fun onVaultsRetrieveSuccess(vaults: ArrayList<Vault>)
    fun onVaultsRetrieveError(e: Exception)
}