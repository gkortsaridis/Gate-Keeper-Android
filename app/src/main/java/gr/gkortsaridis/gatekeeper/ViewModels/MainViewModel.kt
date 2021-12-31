package gr.gkortsaridis.gatekeeper.ViewModels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.EncryptedData
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Repos.UserDataRepository
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): ViewModel() {

    companion object {
        fun filterLoginsByVault(logins: ArrayList<Login>, vault: Vault): ArrayList<Login> {
            return ArrayList(logins.filter { it.vault_id == vault.id })
        }

        fun filterLoginsByCurrentVault(logins: ArrayList<Login>): ArrayList<Login> {
            return filterLoginsByVault(logins, VaultRepository.getLastActiveVault())
        }
    }

    fun getLastActiveVault() = userDataRepository.getLastActiveVault()


    val allVaults = userDataRepository.getAllVaults()
    val allLogins = userDataRepository.getAllLogins()

    val allCards  = userDataRepository.getLocalCards()
    val allNotes  = userDataRepository.getLocalNotes()
    val allDevices = userDataRepository.getLocalDevices()



}