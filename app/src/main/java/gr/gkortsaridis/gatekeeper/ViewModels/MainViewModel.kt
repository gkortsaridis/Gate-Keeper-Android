package gr.gkortsaridis.gatekeeper.ViewModels

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.EncryptedData
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Repos.UserDataRepository
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import javax.inject.Inject
import kotlin.math.log

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

        fun getApplicationInfoByPackageName(packageName: String?, packageManager: PackageManager): ResolveInfo? {
            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            val pkgAppsList: List<ResolveInfo> = packageManager.queryIntentActivities(mainIntent, 0)
            for (app in pkgAppsList) {
                if (app.activityInfo.packageName == packageName) {
                    return app
                }
            }
            return null
        }
    }

    fun getLastActiveVault() = userDataRepository.getLastActiveVault()
    fun getVaultById(id: String) = userDataRepository.getVaultById(id)

    val allVaults = userDataRepository.getAllVaults()
    val allLogins = userDataRepository.getAllLogins()

    val allCards  = userDataRepository.getLocalCards()
    val allNotes  = userDataRepository.getLocalNotes()
    val allDevices = userDataRepository.getLocalDevices()


    fun getAllLoginsLive(observer: LifecycleOwner): LiveData<ArrayList<Login>> {
        val encryptedLogins = userDataRepository.getLocalLoginsLive()

        val decryptedLogins = MutableLiveData<ArrayList<Login>>()
        decryptedLogins.value = ArrayList()

        encryptedLogins.observe(observer) {
            val logins = ArrayList<Login>()
            it.forEach { item ->
                val modifiedItem = EncryptedData(
                    id=item.id,
                    encryptedData = item.encryptedData,
                    iv=item.iv,
                    dateCreated = item.dateCreated,
                    dateModified = item.dateModified
                )
                val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(modifiedItem, Login::class.java) as Login?
                if (decrypted != null) {
                    decrypted.id = item.id
                    decrypted.date_created = item.dateCreated
                    decrypted.date_modified = item.dateModified
                    logins.add(decrypted)
                }
            }
            decryptedLogins.value = logins
        }

        return decryptedLogins
    }



}