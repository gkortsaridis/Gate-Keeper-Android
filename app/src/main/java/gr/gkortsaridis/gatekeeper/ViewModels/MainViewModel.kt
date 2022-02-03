package gr.gkortsaridis.gatekeeper.ViewModels

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Repos.UserDataRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): BaseViewModel(userDataRepository = userDataRepository) {

    companion object {
        fun filterLoginsByVault(logins: ArrayList<Login>, vault: Vault): ArrayList<Login> {
            return ArrayList(logins.filter { it.vault_id == vault.id })
        }

        fun filterCardsByVault(cards: ArrayList<CreditCard>, vault: Vault): ArrayList<CreditCard> {
            return ArrayList(cards.filter { it.vaultId == vault.id })
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



}