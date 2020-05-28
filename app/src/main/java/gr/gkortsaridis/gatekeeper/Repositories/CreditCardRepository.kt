package gr.gkortsaridis.gatekeeper.Repositories

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import gr.gkortsaridis.gatekeeper.Database.GatekeeperDatabase
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
import java.lang.Integer.parseInt
import java.sql.Timestamp

@SuppressLint("CheckResult")
object CreditCardRepository {

    val db = GatekeeperDatabase.getInstance(GateKeeperApplication.instance.applicationContext)

    var allCards: ArrayList<CreditCard>
        get() { return ArrayList(db.dao().allCardsSync) }
        set(cards) { db.dao().truncateCards(); for (card in cards) { db.dao().insertCard(card) } }

    fun addLocalCard(card: CreditCard) { db.dao().insertCard(card) }

    fun removeLocalCard(card: CreditCard) { db.dao().deleteCard(card) }

    fun updateLocalCard(card: CreditCard) { db.dao().updateCard(card) }

    fun filterCardsByVault(cards: List<CreditCard>, vault: Vault) : ArrayList<CreditCard> {
        val vaultIds = arrayListOf<String>()
        VaultRepository.allVaults.forEach { vaultIds.add(it.id) }

        val parentedCards = ArrayList(cards.filter { vaultIds.contains(it.vaultId) })

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

    fun validateCreditCardNumber(value: String): Boolean {
        // remove all non digit characters
        var value = value.replace(" ", "");
        var sum = 0;
        var shouldDouble = false;
        // loop through values starting at the rightmost side
        for (i in value.length -1 downTo 0) {
            var digit = parseInt(value[i].toString());

            if (shouldDouble) {
                digit *= 2
                if (digit > 9) digit -= 9;
            }

            sum += digit;
            shouldDouble = !shouldDouble;
        }

        Log.i("VALIDATOR",value+" "+( (sum % 10) == 0) )

        return (sum % 10) == 0;
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
                        if (it.errorCode == -1) {
                            decryptedCard.modifiedDate = Timestamp(System.currentTimeMillis())
                            listener.onCardUpdated(decryptedCard)
                        }
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
                        if (it.errorCode == -1) {
                            decryptedCard.modifiedDate = Timestamp(System.currentTimeMillis())
                            decryptedCard.createdDate = Timestamp(System.currentTimeMillis())
                            listener.onCreditCardCreated(decryptedCard)
                        }
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
        for (card in allCards) {
            if (card.id == cardId) {
                return card
            }
        }
        return null
    }

}