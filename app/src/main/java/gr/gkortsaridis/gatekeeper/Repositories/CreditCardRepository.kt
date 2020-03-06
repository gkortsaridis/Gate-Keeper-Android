package gr.gkortsaridis.gatekeeper.Repositories

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardDeleteListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardRetrieveListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardUpdateListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
object CreditCardRepository {

    fun filterCardsByVault(vault: Vault) : ArrayList<CreditCard> {
        val vaultIds = arrayListOf<String>()
        GateKeeperApplication.vaults.forEach { vaultIds.add(it.id) }
        val parentedCards = ArrayList(GateKeeperApplication.cards.filter { vaultIds.contains(it.vaultId) })

        if (vault.id == "-1") { return parentedCards }

        val filtered = parentedCards.filter { it.vaultId == vault.id }
        return ArrayList(filtered)
    }

    fun getCreditCardType(cardNumber: String): CardType {

        val VISA_PREFIX = "4"
        val MASTERCARD_PREFIX = "51,52,53,54,55,"
        val DISCOVER_PREFIX = "6011"
        val AMEX_PREFIX = "34,37,"

        return if (cardNumber.isNotEmpty() && cardNumber.substring(0, 1) == VISA_PREFIX) {
            CardType.Visa
        } else if (cardNumber.length >= 2 && MASTERCARD_PREFIX.contains(cardNumber.substring(0, 2) + ",")) {
            CardType.Mastercard
        } else if (cardNumber.length >= 2 && AMEX_PREFIX.contains(cardNumber.substring(0, 2) + ",")) {
            CardType.Amex
        } else if (cardNumber.length >= 4 && cardNumber.substring(0, 4) == DISCOVER_PREFIX) {
            CardType.DiscoverCard
        } else {
            CardType.Unknown
        }
    }

    fun getCreditCardTypeImage(card: CreditCard): Int? {
        return when (card.type) {
            CardType.Unknown -> null
            CardType.DiscoverCard -> R.drawable.discover
            CardType.Amex -> R.drawable.amex
            CardType.Mastercard -> R.drawable.mastercard
            CardType.Visa -> R.drawable.visa
            CardType.DinersClub -> R.drawable.discover
        }
    }

    fun deleteCreditCard(card: CreditCard, listener: CreditCardDeleteListener?) {
        GateKeeperAPI.api.deleteCard(cardId = card.id, body = SecurityRepository.createUsernameHashRequestBody())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    if (it.errorCode == -1 && card.id.toLong() == it.deletedItemID) { listener?.onCardDeleted() }
                    else { listener?.onCardDeleteError(it.errorCode, it.errorMsg) }
                },
                { listener?.onCardDeleteError(it.hashCode(), it.localizedMessage ?: "") }
            )
    }

    fun updateCreditCard(card: CreditCard, listener: CreditCardUpdateListener) {
        GateKeeperAPI.api.updateCard(SecurityRepository.createEncryptedDataRequestBody(card, card.id))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    val decryptedCard = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(it.data, CreditCard::class.java) as CreditCard?
                    if (decryptedCard != null) {
                        decryptedCard.id = it.data.id.toString()
                        if (it.errorCode == -1) { listener.onCardUpdated(decryptedCard) }
                        else { listener.onCardUpdateError(it.errorCode, it.errorMsg) }
                    } else {
                        listener.onCardUpdateError(-1, "Decryption Error")
                    }
                },
                { listener.onCardUpdateError(it.hashCode(), it.localizedMessage ?: "") }
            )
    }

    fun encryptAndStoreCard(activity: Activity, card: CreditCard, listener: CreditCardCreateListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()

        GateKeeperAPI.api.createCard(SecurityRepository.createEncryptedDataRequestBody(card))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    viewDialog.hideDialog()
                    val decryptedCard = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(it.data, CreditCard::class.java) as CreditCard?
                    if (decryptedCard != null) {
                        decryptedCard.id = it.data.id.toString()
                        if (it.errorCode == -1) { listener.onCreditCardCreated(decryptedCard) }
                        else { listener.onCreditCardCreateError(it.errorCode, it.errorMsg) }
                    } else {
                        listener.onCreditCardCreateError(-1, "Decryption Error")
                    }
                },
                {
                    viewDialog.hideDialog()
                    listener.onCreditCardCreateError(it.hashCode(), it.localizedMessage ?: "")
                }
            )
    }

    fun getCreditCardById(cardId: String): CreditCard? {
        for (card in GateKeeperApplication.cards) {
            if (card.id == cardId) {
                return card
            }
        }
        return null
    }

}