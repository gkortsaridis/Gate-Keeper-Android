package gr.gkortsaridis.gatekeeper.ViewModels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Repos.UserDataRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
): ViewModel() {

    companion object {
        fun filterLoginsByVault(logins: ArrayList<Login>, vault: Vault): ArrayList<Login> {
            val vaultIds = arrayListOf<String>()
            VaultRepository.allVaults.forEach { vaultIds.add(it.id) }
            val parentedLogins = ArrayList(logins.filter { vaultIds.contains(it.vault_id) })

            if (vault.id == "-1") { return parentedLogins }

            val filtered = parentedLogins.filter {
                it.vault_id == vault.id
            }

            return ArrayList(filtered)
        }

        fun filterLoginsByCurrentVault(logins: ArrayList<Login>): ArrayList<Login> {
            return filterLoginsByVault(logins, VaultRepository.getLastActiveVault())
        }
    }

    val allVaults = userDataRepository.getLocalVaults()
    val allLogins = userDataRepository.getLocalLogins()
    val allCards  = userDataRepository.getLocalCards()
    val allNotes  = userDataRepository.getLocalNotes()
    val allDevices = userDataRepository.getLocalDevices()



}