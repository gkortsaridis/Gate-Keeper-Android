package gr.gkortsaridis.gatekeeper.UI.Cards


import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.wajahatkarim3.easyflipview.EasyFlipView
import com.whiteelephant.monthpicker.MonthPickerDialog
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Utils.dp
import java.util.*


class CardInfoFragment(private val card: CreditCard) : DialogFragment() {

    private lateinit var cardNumberET: EditText
    private lateinit var cardholderNameET: EditText
    private lateinit var expiryDateET: EditText
    private lateinit var cvvET: EditText
    private lateinit var flipToBack: LinearLayout
    private lateinit var flipToFront: LinearLayout
    private lateinit var flipCard: EasyFlipView
    private lateinit var saveBtn: LinearLayout
    private lateinit var cancelBtn: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_card_info, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        flipCard = view.findViewById(R.id.flip_view)
        flipToBack = view.findViewById(R.id.flip_to_back)
        flipToFront = view.findViewById(R.id.flip_to_front)
        cardNumberET = view.findViewById(R.id.card_number_et)
        cardholderNameET = view.findViewById(R.id.cardholder_name_et)
        expiryDateET = view.findViewById(R.id.expiry_date_et)
        saveBtn = view.findViewById(R.id.save_btn)
        cancelBtn = view.findViewById(R.id.cancel_btn)

        cardNumberET.setText(card.number)
        cardholderNameET.setText(card.cardholderName)
        expiryDateET.setText(card.expirationDate)
        flipToBack.setOnClickListener { flipCard.flipTheView() }
        flipToFront.setOnClickListener { flipCard.flipTheView() }
        saveBtn.setOnClickListener { saveCard() }
        cancelBtn.setOnClickListener { dismiss() }

        return view
    }

    private fun saveCard() {

    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(resources.displayMetrics.widthPixels, 400.dp)
    }

    private fun showVaultSelectorPicker() {
        //vaultET.clearFocus()
        //vaultET.hideKeyboard()

        val vaults = GateKeeperApplication.vaults
        val vaultNames = arrayOfNulls<String>(vaults.size)
        var selected = -1
        vaults.forEachIndexed { index, vault -> vaultNames[index] = vault.name }
        //vaults.forEachIndexed { index, vault -> if(vaultET.text.toString() == vault.name) selected = index }


        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Select Card Vault")
        builder.setSingleChoiceItems(vaultNames, selected) { dialog, which ->
            //vaultET.setText(vaultNames[which])
            //vaultET.clearFocus()
            //vaultET.hideKeyboard()
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.setOnCancelListener {
            //vaultET.clearFocus()
            //vaultET.hideKeyboard()
        }
        dialog.show()
    }

    private fun showMonthYearPicker() {
        //expirationDateET.clearFocus()
        //expirationDateET.hideKeyboard()

        val builder = MonthPickerDialog.Builder(activity,
            MonthPickerDialog.OnDateSetListener { selectedMonth, selectedYear ->
                val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec")
                //expirationDateET.setText("${months[selectedMonth]}/$selectedYear")
                //expirationDateET.clearFocus()
                //expirationDateET.hideKeyboard()
            }, 2020, Calendar.JANUARY)

        val dialog = builder.setActivatedMonth(Calendar.JULY)
            .setMinYear(2020)
            .setActivatedYear(2020)
            .setMaxYear(2030)
            .setActivatedYear(2020)
            .setActivatedMonth(4)
            .setTitle("Select Card Expiration Date")
            .build()

        dialog.setOnCancelListener {
            //expirationDateET.clearFocus()
            //expirationDateET.hideKeyboard()
        }
        dialog.setOnDismissListener {
            //expirationDateET.clearFocus()
            //expirationDateET.hideKeyboard()
        }
        dialog.show()

    }

}
