package gr.gkortsaridis.gatekeeper.Database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.Vault

class MainViewModel(application: Application) :
    AndroidViewModel(application) {

    var appLogins: LiveData<List<Login>>
    var appVaults: LiveData<List<Vault>>
    val appCards: LiveData<List<CreditCard>>
    val appNotes: LiveData<List<Note>>

    init {
        val gatekeeperDatabase = GatekeeperDatabase.getInstance(application.applicationContext)
        appLogins = gatekeeperDatabase.dao().allLogins
        appVaults = gatekeeperDatabase.dao().allVaults
        appCards  = gatekeeperDatabase.dao().allCards
        appNotes  = gatekeeperDatabase.dao().allNotes
    }
}