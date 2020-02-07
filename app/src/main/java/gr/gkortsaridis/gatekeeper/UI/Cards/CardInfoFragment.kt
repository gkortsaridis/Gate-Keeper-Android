package gr.gkortsaridis.gatekeeper.UI.Cards


import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.text.trimmedLength
import androidx.fragment.app.DialogFragment
import com.wajahatkarim3.easyflipview.EasyFlipView
import com.whiteelephant.monthpicker.MonthPickerDialog
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardCreateListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardDeleteListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardUpdateListener
import gr.gkortsaridis.gatekeeper.Interfaces.MyDialogFragmentListeners
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.CreditCardRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.Utils.CreditCardFormattingTextWatcher
import gr.gkortsaridis.gatekeeper.Utils.dp
import java.util.*


class CardInfoFragment(private var card: CreditCard?, private val isCreate: Boolean, private val listeners: MyDialogFragmentListeners) : DialogFragment(), TextWatcher {

    private lateinit var cardVault: Vault

    private lateinit var cardNumberET: EditText
    private lateinit var cardholderNameET: EditText
    private lateinit var expiryDateTV: TextView
    private lateinit var cvvET: EditText
    private lateinit var flipToBack: LinearLayout
    private lateinit var flipToFront: LinearLayout
    private lateinit var flipCard: EasyFlipView
    private lateinit var saveBtn: LinearLayout
    private lateinit var cancelBtn: LinearLayout
    private lateinit var vaultContainer: LinearLayout
    private lateinit var vaultName: TextView
    private lateinit var expiryDateContainer: LinearLayout
    private lateinit var cardNickname: EditText
    private lateinit var saveText: TextView
    private lateinit var saveIcon: ImageView
    private lateinit var deleteCardBtn: LinearLayout

    private var backCardShown: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_card_info, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (isCreate) {
            card = CreditCard(
                id = "",
                cardName = "",
                type = CardType.Unknown,
                number = "",
                expirationDate = "",
                cvv = "",
                cardholderName = "",
                vaultId = VaultRepository.getLastActiveRealVault().id,
                accountId = AuthRepository.getUserID()
            )
            cardVault = VaultRepository.getLastActiveRealVault()
        } else {
            cardVault = VaultRepository.getVaultByID(card!!.vaultId)!!
        }

        flipCard = view.findViewById(R.id.flip_view)
        flipToBack = view.findViewById(R.id.flip_to_back)
        flipToFront = view.findViewById(R.id.flip_to_front)
        cardNumberET = view.findViewById(R.id.card_number_et)
        cardholderNameET = view.findViewById(R.id.cardholder_name_et)
        expiryDateTV = view.findViewById(R.id.expiry_date_et)
        cvvET = view.findViewById(R.id.cvv_ET)
        saveBtn = view.findViewById(R.id.save_btn)
        cancelBtn = view.findViewById(R.id.cancel_btn)
        vaultContainer = view.findViewById(R.id.vault_view)
        vaultName = view.findViewById(R.id.vault_name)
        expiryDateContainer = view.findViewById(R.id.expiry_date_view)
        cardNickname = view.findViewById(R.id.card_alias_ET)
        saveText = view.findViewById(R.id.save_text)
        saveIcon = view.findViewById(R.id.save_icon)
        deleteCardBtn = view.findViewById(R.id.card_delete_btn)

        cardNumberET.setText(card?.number)
        cardholderNameET.setText(card?.cardholderName)
        cardNickname.setText(card?.cardName)
        cvvET.setText(card?.cvv)
        expiryDateTV.text = card?.expirationDate
        vaultName.text = cardVault.name

        flipToBack.setOnClickListener { flipCard.flipTheView() }
        flipToFront.setOnClickListener { flipCard.flipTheView() }
        cancelBtn.setOnClickListener { dismiss() }
        vaultContainer.setOnClickListener { showVaultSelectorPicker() }
        expiryDateContainer.setOnClickListener { showMonthYearPicker() }
        deleteCardBtn.setOnClickListener { deleteCard() }

        cardNumberET.addTextChangedListener(this)
        cardholderNameET.addTextChangedListener(this)
        cardNickname.addTextChangedListener(this)
        cvvET.addTextChangedListener(this)
        cardNumberET.addTextChangedListener(CreditCardFormattingTextWatcher(cardNumberET))

        toggleSaveButton()
        return view
    }

    private fun deleteCard() {
        val viewDialog = ViewDialog(activity!!)
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Card Delete")
        builder.setMessage("Are you sure you want to delete this card?")
        builder.setPositiveButton("YES") { dialog, _ ->
            viewDialog.showDialog()
            CreditCardRepository.deleteCreditCard(card!!, object: CreditCardDeleteListener{
                override fun onCardDeleted() {
                    GateKeeperApplication.cards.remove(card!!)
                    viewDialog.hideDialog()
                    dialog.dismiss()
                    Toast.makeText(context, getString(R.string.card_deleted), Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            })
        }
        builder.setNegativeButton("NO") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun saveCard() {
        card?.number = cardNumberET.text.toString()
        card?.cardholderName = cardholderNameET.text.toString()
        card?.expirationDate = expiryDateTV.text.toString()
        card?.cardName = cardNickname.text.toString()
        card?.vaultId = cardVault.id
        card?.cvv = cvvET.text.toString()

        val viewDialog = ViewDialog(activity!!)
        if (isCreate) {
            CreditCardRepository.encryptAndStoreCard(activity!!, card!!, object: CreditCardCreateListener{
                override fun onCreditCardCreated(card: CreditCard) {
                    GateKeeperApplication.cards.add(card)
                    Toast.makeText(context, getString(R.string.card_created), Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            })
        }else {
            viewDialog.showDialog()
            CreditCardRepository.updateCreditCard(card!!, object: CreditCardUpdateListener{
                override fun onCardUpdated(card: CreditCard) {
                    viewDialog.hideDialog()
                    GateKeeperApplication.cards.replaceAll { if (it.id == card.id) card else it }
                    dismiss()
                    Toast.makeText(context, getString(R.string.card_updated), Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(resources.displayMetrics.widthPixels, 400.dp)
    }

    private fun showVaultSelectorPicker() {

        val vaults = GateKeeperApplication.vaults
        val vaultNames = arrayOfNulls<String>(vaults.size)
        var selected = -1
        vaults.forEachIndexed { index, vault -> vaultNames[index] = vault.name }
        vaults.forEachIndexed { index, vault -> if(cardVault.id == vault.id) selected = index }

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Select Card Vault")
        builder.setSingleChoiceItems(vaultNames, selected) { dialog, which ->
            cardVault = vaults[which]
            vaultName.text = cardVault.name
            dialog.dismiss()
            toggleSaveButton()
        }
        val dialog = builder.create()
        dialog.setOnCancelListener { }
        dialog.show()
    }

    private fun showMonthYearPicker() {
        val builder = MonthPickerDialog.Builder(activity,
            MonthPickerDialog.OnDateSetListener { selectedMonth, selectedYear ->
                val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec")
                expiryDateTV.text = "${months[selectedMonth]}/$selectedYear"
                toggleSaveButton()
            }, 2020, Calendar.JANUARY)

        val dialog = builder.setActivatedMonth(Calendar.JULY)
            .setMinYear(2020)
            .setActivatedYear(2020)
            .setMaxYear(2030)
            .setActivatedYear(2020)
            .setActivatedMonth(4)
            .setTitle("Select Card Expiration Date")
            .build()

        dialog.setOnCancelListener {}
        dialog.setOnDismissListener {}
        dialog.show()
    }

    private fun toggleSaveButton() {
        if (isCreate) {

            if(!backCardShown) {
                saveBtn.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                saveBtn.setOnClickListener {
                    flipCard.flipTheView()
                    backCardShown = true
                    toggleSaveButton()
                }
                saveText.text = "Next"
            } else {
                saveBtn.setBackgroundColor(resources.getColor(R.color.done_green))
                saveBtn.setOnClickListener { saveCard() }
                saveText.text = "Save"
            }

        } else {

            if (cardShouldSave()) {
                if (cardDataAreValid()) {
                    saveBtn.setBackgroundColor(resources.getColor(R.color.done_green))
                    saveBtn.setOnClickListener { saveCard() }
                    saveText.text = "Save"
                } else {
                    saveBtn.setBackgroundColor(resources.getColor(R.color.greyish))
                    saveBtn.setOnClickListener { }
                    saveText.text = "Card Data not valid"
                }
            } else {
                saveBtn.setBackgroundColor(resources.getColor(R.color.greyish))
                saveBtn.setOnClickListener { }
                saveText.text = "Nothing to save"
            }

        }

    }

    private fun cardDataAreValid(): Boolean {
        return (cardNumberET.text.toString().replace(" ","").length == 16
                && (if (card?.type == CardType.Amex) cvvET.text.toString().length == 4 else cvvET.text.toString().length == 3) //CVV : 4 lenght for AMEX, 3 for rest
                )
    }

    private fun cardShouldSave(): Boolean {
        return (card?.cardholderName != cardholderNameET.text.toString()
                || card?.number != cardNumberET.text.toString()
                || card?.expirationDate != expiryDateTV.text.toString()
                || card?.cvv != cvvET.text.toString()
                || card?.cardName != cardNickname.text.toString()
                || card?.vaultId != cardVault.id)
    }

    override fun afterTextChanged(s: Editable?) { toggleSaveButton() }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listeners.onDismissed()
    }
}
