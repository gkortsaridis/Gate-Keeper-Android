package gr.gkortsaridis.gatekeeper.UI.Cards

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import com.maxpilotto.actionedittext.ActionEditText
import com.maxpilotto.actionedittext.actions.Icon
import com.whiteelephant.monthpicker.MonthPickerDialog
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardDeleteListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardUpdateListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.CreditCardRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import io.noties.tumbleweed.Tween
import io.noties.tumbleweed.android.ViewTweenManager
import io.noties.tumbleweed.android.types.Alpha
import kotlinx.android.synthetic.main.activity_card_edit.*
import kotlinx.android.synthetic.main.activity_card_edit.activity_title
import kotlinx.android.synthetic.main.activity_card_edit.save_arc
import kotlinx.android.synthetic.main.activity_card_edit.save_update_button
import kotlinx.android.synthetic.main.activity_card_edit.vault_icon
import kotlinx.android.synthetic.main.activity_card_edit.vault_name
import kotlinx.android.synthetic.main.activity_create_login.toolbar
import kotlinx.android.synthetic.main.activity_create_login.vault_view
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.util.*

class CardEditActivity : AppCompatActivity() {

    private val TAG = "_Create_Login_Activity_"

    private var vaultToAdd: Vault? = null
    private lateinit var card: CreditCard

    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_edit)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        delete_card_btn.setOnClickListener { deleteCard() }

        vault_view.setOnClickListener {
            card.cardName = card_name_et.text.toString()
            card.cardholderName = cardholder_name_et.text.toString()
            card.number = card_number_et.text.toString()
            card.expirationDate = expiration_date_tv.text.toString()
            card.cvv = cvv_et.text.toString()

            val intent = Intent(this, SelectVaultActivity::class.java)
            intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_VAULT)
            intent.putExtra("vault_id",vaultToAdd?.id)
            startActivityForResult(intent, GateKeeperConstants.CHANGE_VAULT_REQUEST_CODE)
        }

        this.activity = this

        val cardId = intent.getStringExtra("card_id")
        if (cardId == null) {
            activity_title.text = "Create new Card"
            vaultToAdd = VaultRepository.getLastActiveRealVault()
            save_update_button.setOnClickListener {
                if (cardDataAreValid()) {
                    createCard()
                } else {
                    Toast.makeText(this, "All Card fields must be filled", Toast.LENGTH_SHORT).show()
                }
            }

            val year = Calendar.getInstance().get(Calendar.YEAR).toString()
            val month = Calendar.getInstance().get(Calendar.MONTH) + 1
            val monthStr = (if (month < 10) "0$month" else month.toString())

            card = CreditCard(
                id = "-1",
                cardName = "",
                type = CardType.Unknown,
                number = "",
                expirationDate = monthStr+"/"+year.substring(2,4),
                cvv = "",
                cardholderName = "",
                vaultId = vaultToAdd!!.id,
                accountId = AuthRepository.getUserID(),
                modifiedDate = null
            )
        } else {
            card = CreditCardRepository.getCreditCardById(cardId)!!
            activity_title.text = "Edit Card"
            vaultToAdd = VaultRepository.getVaultByID(card.vaultId)!!
            save_update_button.setOnClickListener {
                if (cardDataAreValid()) {
                    updateCard()
                } else {
                    Toast.makeText(this, "All Card fields must be filled", Toast.LENGTH_SHORT).show()
                }
            }
        }

        KeyboardVisibilityEvent.setEventListener(activity) { isOpen ->
            Tween
                .to(save_arc, Alpha.VIEW, 0.3F)
                .target( if (isOpen) 0.0f else 1.0f)
                .start(ViewTweenManager.get(save_arc))

            save_update_button.visibility = if (isOpen) View.GONE else View.VISIBLE
        }

        cardholder_name_et.apply{    // Kotlin
            action(Icon(context).apply {
                icon = R.drawable.copy
                onClick = {
                    copy(cardholder_name_et.text.toString(), "Cardholder name")
                }
            })

            showActions()   // This displays all the actions that are added
        }

        card_number_et.apply{    // Kotlin
            action(Icon(context).apply {
                icon = R.drawable.copy
                onClick = {
                    copy(card_number_et.text.toString(), "Card number")
                }
            })

            showActions()   // This displays all the actions that are added
        }

        expiration_date_container.setOnClickListener {
            showMonthYearPicker()
        }

        copy_expiry_date.setOnClickListener { copy(expiration_date_tv.text.toString(), "Expiration date") }

        cvv_et.apply{    // Kotlin
            action(Icon(context).apply {
                icon = R.drawable.copy
                onClick = {
                    copy(cvv_et.text.toString(), "CVV")
                }
            })

            showActions()   // This displays all the actions that are added
        }

        getEditTextFromView(card_number_et).addTextChangedListener {
            card.number = it.toString()
            card_number_et.tintAllOnError = true
            card_number_et.error = if (isCardNumberCorrect(it.toString())) null else "Incorrect card number"
            card.type = CreditCardRepository.getCreditCardType(card.number)
            setActionButton()
            toggleSaveButton()
        }
        getEditTextFromView(card_number_et).addTextChangedListener { toggleSaveButton() }
        getEditTextFromView(cvv_et).addTextChangedListener { toggleSaveButton() }

        updateUI()
        setActionButton()
        toggleSaveButton()
    }

    private fun toggleSaveButton() {
        save_update_button.setBackgroundColor(resources.getColor(
            if (cardDataAreFilled()) R.color.colorPrimaryDark else R.color.greyish
        ))
    }

    private fun setActionButton() {
        save_update_button.setBackgroundResource(if (isCardNumberCorrect(card.number)) R.color.colorPrimaryDark else R.color.greyish)
    }

    private fun showMonthYearPicker() {
        val builder = MonthPickerDialog.Builder(activity,
            MonthPickerDialog.OnDateSetListener { selectedMonth, selectedYear ->
                val formattedMonth = if (selectedMonth+1 < 10) "0"+(selectedMonth+1) else (selectedMonth+1).toString()
                expiration_date_tv.text = formattedMonth+"/"+selectedYear.toString().substring(2,4)
                cvv_et.requestFocus()
            }, 2020, Calendar.JANUARY)

        val year = ("20"+expiration_date_tv.text.split("/")[1]).toInt()
        val month = (expiration_date_tv.text.split("/")[0]).toInt() - 1

        val dialog = builder.setActivatedMonth(Calendar.JULY)
            .setMinYear(2019)
            .setMaxYear(2030)
            .setActivatedYear(year)
            .setActivatedMonth(month)
            .setTitle("Select Card Expiration Date")
            .build()

        dialog.setOnCancelListener {}
        dialog.setOnDismissListener {}
        dialog.show()
    }

    private fun updateUI() {
        card_name_et.setText(card.cardName)
        cardholder_name_et.apply {
            text = card.cardholderName
        }
        card_number_et.apply {
            text = card.number
        }
        expiration_date_tv.apply {
            text = card.expirationDate
        }
        cvv_et.apply {
            text = card.cvv
        }

        delete_card_btn.visibility = if (card.id != "-1") View.VISIBLE else View.GONE

        vault_name.text = vaultToAdd?.name
        vault_view.setBackgroundColor(resources.getColor(vaultToAdd?.getVaultColorResource() ?: R.color.colorPrimaryDark))
        vault_name.setTextColor(resources.getColor(vaultToAdd?.getVaultColorAccent() ?: R.color.colorPrimaryDark))
        vault_icon.setColorFilter(resources.getColor(vaultToAdd?.getVaultColorAccent() ?: R.color.colorPrimaryDark))
    }

    private fun copy(txt: String, what: String) {
        val clipboard = ContextCompat.getSystemService(
            this,
            ClipboardManager::class.java
        ) as ClipboardManager
        val clip = ClipData.newPlainText("label",txt)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "$what copied", Toast.LENGTH_SHORT).show()
    }

    private fun deleteCard() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Card")
        builder.setMessage("Are you sure you want to delete this Card item?")
        builder.setPositiveButton("DELETE"){dialog, _ ->
            dialog.cancel()

            val viewDialog = ViewDialog(activity)
            viewDialog.showDialog()
            CreditCardRepository.deleteCreditCard(card, object: CreditCardDeleteListener {
                override fun onCardDeleted() {
                    GateKeeperApplication.cards.remove(card)
                    viewDialog.hideDialog()
                    dialog.dismiss()
                    Toast.makeText(activity, getString(R.string.card_deleted), Toast.LENGTH_SHORT).show()
                    activity.finish()
                }
            })

        }
        builder.setNegativeButton("CANCEL"){dialog, _ -> dialog.cancel() }
        val dialog: AlertDialog = builder.create()
        dialog.show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(resources.getColor(R.color.error_red))
    }

    private fun updateCard() {
        card.number = card_number_et.text.toString()
        card.cardholderName = cardholder_name_et.text.toString()
        card.expirationDate = expiration_date_tv.text.toString()
        card.cardName = card_name_et.text.toString()
        card.vaultId = vaultToAdd!!.id
        card.cvv = cvv_et.text.toString()
        card.type = CreditCardRepository.getCreditCardType(card.number)
        card.modifiedDate = null

        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()
        CreditCardRepository.updateCreditCard(card, object: CreditCardUpdateListener {
            override fun onCardUpdated(card: CreditCard) {
                viewDialog.hideDialog()
                GateKeeperApplication.cards.replaceAll { if (it.id == card.id) card else it }
                activity.finish()
                Toast.makeText(activity, getString(R.string.card_updated), Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun createCard() {
        card.number = card_number_et.text.toString()
        card.cardholderName = cardholder_name_et.text.toString()
        card.expirationDate = expiration_date_tv.text.toString()
        card.cardName = card_name_et.text.toString()
        card.vaultId = vaultToAdd!!.id
        card.cvv = cvv_et.text.toString()
        card.type = CreditCardRepository.getCreditCardType(card.number)
        card.modifiedDate = null

        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()
        CreditCardRepository.encryptAndStoreCard(activity, card, object:
            CreditCardCreateListener {
            override fun onCreditCardCreated(card: CreditCard) {
                viewDialog.hideDialog()
                GateKeeperApplication.cards.add(card)
                Toast.makeText(activity, getString(R.string.card_created), Toast.LENGTH_SHORT).show()
                activity.finish()
            }
        })
    }

    private fun cardDataAreFilled(): Boolean {
        return (card_number_et.text!!.isNotBlank()
                && cardholder_name_et.text!!.isNotBlank()
                && expiration_date_tv.text.isNotBlank()
                && cvv_et.text!!.isNotBlank())
    }

    private fun isCardNumberCorrect(number: String): Boolean {
        return CreditCardRepository.validateCreditCardNumber(number)
    }

    private fun cardDataAreValid(): Boolean {
        return (
                CreditCardRepository.validateCreditCardNumber(card_number_et.text.toString())
                        && (if (card.type == CardType.Amex) cvv_et.text.toString().length == 4 else cvv_et.text.toString().length == 3)
                )
    }

    private fun getEditTextFromView(view: ActionEditText): AppCompatEditText {
        //Get the EditText View from the custom View
        val linearLayout = view[0] as LinearLayout
        val relativeLayout = linearLayout[1] as RelativeLayout
        return relativeLayout[0] as AppCompatEditText
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GateKeeperConstants.CHANGE_VAULT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val vaultId = data!!.data.toString()
            vaultToAdd = VaultRepository.getVaultByID(vaultId)
            updateUI()
        }

    }
}
