package gr.gkortsaridis.gatekeeper.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.Repos.UserDataRepository
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import gr.gkortsaridis.gatekeeper.Utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): ViewModel() {

    // ~~~~~~~~~~~~ API CALLS  ~~~~~~~~~~~~
    private val compositeDisposable = CompositeDisposable()
    val allData = MutableLiveData<Resource<AllData>>()

    fun getAllData(userId:String): LiveData<Resource<AllData>> {
        allData.postValue(Resource.loading(data = null))

        compositeDisposable.add(userDataRepository.getAllData(userId).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if(it.errorCode == -1 && it.data != null) {
                        val vaultsEncrypted = it.data.vaults
                        val loginsEncrypted = it.data.logins
                        val cardsEncrypted = it.data.cards
                        val notesEncrypted = it.data.notes
                        val devicesEncrypted = it.data.devices

                        val vaults = ArrayList<Vault>()
                        val logins = ArrayList<Login>()
                        val cards = ArrayList<CreditCard>()
                        val notes = ArrayList<Note>()
                        val devices = ArrayList<Device>()

                        val dbItems = ArrayList<EncryptedDBItem>()

                        for (vault in vaultsEncrypted) {
                            val dbItem = EncryptedDBItem(
                                id=vault.id,
                                type=0,
                                encryptedData = vault.encryptedData,
                                iv = vault.iv,
                                dateCreated = vault.dateCreated,
                                dateModified = vault.dateModified
                            )
                            dbItems.add(dbItem)
                            /*val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(vault, Vault::class.java) as Vault?
                            if (decrypted != null) {
                                decrypted.id = vault.id.toString()
                                decrypted.dateCreated = vault.dateCreated
                                decrypted.dateModified = vault.dateModified
                                vaults.add(decrypted)
                            }*/
                        }

                        for (login in loginsEncrypted) {
                            val dbItem = EncryptedDBItem(
                                id=login.id,
                                type=1,
                                encryptedData = login.encryptedData,
                                iv = login.iv,
                                dateCreated = login.dateCreated,
                                dateModified = login.dateModified
                            )
                            dbItems.add(dbItem)

                            /*val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(login, Login::class.java) as Login?
                            if (decrypted != null) {
                                decrypted.id = login.id.toString()
                                decrypted.date_created = login.dateCreated
                                decrypted.date_modified = login.dateModified
                                logins.add(decrypted)
                            }*/
                        }

                        for (card in cardsEncrypted) {
                            val dbItem = EncryptedDBItem(
                                id=card.id,
                                type=2,
                                encryptedData = card.encryptedData,
                                iv = card.iv,
                                dateCreated = card.dateCreated,
                                dateModified = card.dateModified
                            )
                            dbItems.add(dbItem)

                            /*val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(card, CreditCard::class.java) as CreditCard?
                            if (decrypted != null) {
                                decrypted.id = card.id.toString()
                                decrypted.modifiedDate = card.dateModified
                                decrypted.createdDate = card.dateCreated
                                cards.add(decrypted)
                            }*/
                        }

                        for (note in notesEncrypted) {
                            val dbItem = EncryptedDBItem(
                                id=note.id,
                                type=3,
                                encryptedData = note.encryptedData,
                                iv = note.iv,
                                dateCreated = note.dateCreated,
                                dateModified = note.dateModified
                            )
                            dbItems.add(dbItem)

                            /*val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(note, Note::class.java) as Note?
                            if (decrypted != null) {
                                decrypted.id = note.id.toString()
                                decrypted.createDate = note.dateCreated
                                decrypted.modifiedDate = note.dateModified
                                notes.add(decrypted)
                            }*/
                        }

                        for (device in devicesEncrypted) {
                            val dbItem = EncryptedDBItem(
                                id=device.id,
                                type=4,
                                encryptedData = device.encryptedData,
                                iv = device.iv,
                                dateCreated = device.dateCreated,
                                dateModified = device.dateModified
                            )
                            dbItems.add(dbItem)

                            /*val decrypted = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(device, Device::class.java) as Device?
                            if (decrypted != null) {
                                decrypted.id = device.id.toString()
                                devices.add(decrypted)
                            }*/
                        }

                        /*userDataRepository.updateLogins(logins = logins)
                        userDataRepository.updateVaults(vaults = vaults)
                        userDataRepository.updateCards(cards = cards)
                        userDataRepository.updateNotes(notes = notes)
                        userDataRepository.updateDevices(devices = devices)*/

                        userDataRepository.setUserData(dbItems = dbItems)

                        allData.postValue(Resource.success(data = it.data))
                    } else {
                        allData.postValue(Resource.error(msg = it.errorMsg ?: "Unknown Error", data = null))
                    }
                },
                {
                    allData.postValue(Resource.error(it.localizedMessage ?: "Unknown Error", data = null))
                }
            )
        )

        return allData
    }
}