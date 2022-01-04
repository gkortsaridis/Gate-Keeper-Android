package gr.gkortsaridis.gatekeeper.ViewModels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Repos.UserDataRepository
import javax.inject.Inject


@HiltViewModel
class LoginDetailsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): ViewModel() {

    fun getLastActiveVault() = userDataRepository.getLastActiveVault()
    fun getLoginById(id: String?) = userDataRepository.getLoginById(id)

}