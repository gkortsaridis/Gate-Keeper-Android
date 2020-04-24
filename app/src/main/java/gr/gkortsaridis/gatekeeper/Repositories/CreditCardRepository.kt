package gr.gkortsaridis.gatekeeper.Repositories

import android.annotation.SuppressLint
import android.app.Activity
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardDeleteListener
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
        val ptVisa = "^4[0-9]{6,}$".toRegex()
        val ptMasterCard = "^5[1-5][0-9]{5,}$".toRegex()
        val ptAmeExp = "^3[47][0-9]{5,}$".toRegex()
        val ptDinClb = "^3(?:0[0-5]|[68][0-9])[0-9]{4,}$".toRegex()
        val ptDiscover = "^6(?:011|5[0-9]{2})[0-9]{3,}$".toRegex()
        val ptJcb = "^(?:2131|1800|35[0-9]{3})[0-9]{3,}$".toRegex()

        return when {
            cardNumber.replace(" ","").matches(ptVisa) -> { CardType.Visa }
            cardNumber.replace(" ","").matches(ptMasterCard) -> { CardType.Mastercard }
            cardNumber.replace(" ","").matches(ptAmeExp) -> { CardType.Amex }
            cardNumber.replace(" ","").matches(ptDinClb) -> { CardType.DinersClub }
            cardNumber.replace(" ","").matches(ptDiscover) -> { CardType.DiscoverCard }
            cardNumber.replace(" ","").matches(ptJcb) -> { CardType.JCB }
            else -> { CardType.Unknown }
        }
    }

    fun validateCreditCardNumber(str: String): Boolean {
        val newStr = str.replace(" ","")
        val ints = IntArray(newStr.length)
        for (i in str.indices) { ints[i] = newStr.substring(i, i + 1).toInt() }
        var i = ints.size - 2
        while (i >= 0) {
            var j = ints[i]
            j *= 2
            if (j > 9) { j = j % 10 + 1 }
            ints[i] = j
            i -= 2
        }
        var sum = 0
        for (ind in ints.indices) { sum += ints[ind] }
        return (sum % 10 == 0)
    }

    fun getCreditCardTypeImage(card: CreditCard): Int? {
        return when (card.type) {
            CardType.Unknown -> null
            CardType.DiscoverCard -> R.drawable.discover
            CardType.Amex -> R.drawable.amex
            CardType.Mastercard -> R.drawable.mastercard
            CardType.Visa -> R.drawable.visa
            CardType.DinersClub -> R.drawable.diners
            CardType.JCB -> R.drawable.visa
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