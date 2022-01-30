package gr.gkortsaridis.gatekeeper.Utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.VaultColor

object GateKeeperDevelopMockData {
    val mockVault = Vault(id="10", account_id="1", name="Personal", color= VaultColor.Red, dateCreated=123L, dateModified=234L)
    val mockLogin = Login(account_id = "1", name = "My login", username = "gkortsaridis@gmail.com", password = "pass", url = "www.google.com", notes = "Some Notes", date_created=123L, date_modified=234L, vault_id= mockVault.id)
    val mockLogins = arrayListOf(mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin, mockLogin)
    val mockLoginsLive = MutableLiveData<ArrayList<Login>>()
    val mockNoLogins = arrayListOf<Login>()
}