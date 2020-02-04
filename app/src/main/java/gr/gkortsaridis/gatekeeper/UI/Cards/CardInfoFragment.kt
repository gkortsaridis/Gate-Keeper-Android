package gr.gkortsaridis.gatekeeper.UI.Cards


import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.wajahatkarim3.easyflipview.EasyFlipView
import com.whiteelephant.monthpicker.MonthPickerDialog
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.Utils.dp
import java.util.*


class CardInfoFragment(private val card: CreditCard) : DialogFragment(), TextWatcher {

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_card_info, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        cardVault = VaultRepository.getVaultByID(card.vaultId)!!

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

        cardNumberET.setText(card.number)
        cardholderNameET.setText(card.cardholderName)
        cardNickname.setText(card.cardName)
        cvvET.setText(card.cvv)
        expiryDateTV.text = card.expirationDate
        vaultName.text = cardVault.name

        flipToBack.setOnClickListener { flipCard.flipTheView() }
        flipToFront.setOnClickListener { flipCard.flipTheView() }
        cancelBtn.setOnClickListener { dismiss() }
        vaultContainer.setOnClickListener { showVaultSelectorPicker() }
        expiryDateContainer.setOnClickListener { showMonthYearPicker() }

        cardNumberET.addTextChangedListener(this)
        cardholderNameET.addTextChangedListener(this)
        cardNickname.addTextChangedListener(this)
        cvvET.addTextChangedListener(this)

        toggleSaveButton()
        return view
    }

    private fun saveCard() {
        dismiss()
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
        if (cardShouldSave()) {
            saveBtn.setBackgroundColor(resources.getColor(R.color.done_green))
            saveBtn.setOnClickListener { saveCard() }
            saveText.text = "Save"
        } else {
            saveBtn.setBackgroundColor(resources.getColor(R.color.greyish))
            saveBtn.setOnClickListener { }
            saveText.text = "Nothing to save"
        }
    }

    private fun cardShouldSave(): Boolean {
        return (card.cardholderName != cardholderNameET.text.toString()
                || card.number != cardNumberET.text.toString()
                || card.expirationDate != expiryDateTV.text.toString()
                || card.cvv != cvvET.text.toString()
                || card.cardName != cardNickname.text.toString()
                || card.vaultId != cardVault.id)
    }


    override fun afterTextChanged(s: Editable?) { toggleSaveButton() }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}


}
