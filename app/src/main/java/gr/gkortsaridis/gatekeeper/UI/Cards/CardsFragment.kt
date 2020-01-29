package gr.gkortsaridis.gatekeeper.UI.Cards


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.littlemango.stacklayoutmanager.StackLayoutManager
import com.whiteelephant.monthpicker.MonthPickerDialog
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.CreditCardRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.CreditCardsRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.*
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants.CARD_STATE_DONE
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants.CARD_STATE_EDITED
import java.util.*
import kotlin.collections.ArrayList


class CardsFragment(private var activity: Activity) : Fragment(), CreditCardClickListener {

    private lateinit var cardsRecyclerView: RecyclerView
    private lateinit var addCreditCard: FloatingActionButton
    private lateinit var cardsAdapter: CreditCardsRecyclerViewAdapter
    private lateinit var vaultView: LinearLayout
    private lateinit var vaultName: TextView
    private lateinit var addCardButton: Button
    private lateinit var noCardsMessage: LinearLayout
    private lateinit var cardholderNameET: EditText
    private lateinit var cardNumberET: EditText
    private lateinit var expirationDateET: EditText
    private lateinit var vaultET: EditText
    private lateinit var cvvET: EditText
    private lateinit var cardNicknameET: EditText

    private lateinit var currentVault: Vault

    private lateinit var filtered: ArrayList<CreditCard>
    private lateinit var cardStates: ArrayList<Int>
    private var activeCard : CreditCard? = null
    private val rvDisabler = RecyclerViewDisabler()
    private lateinit var inputLineBackground: Drawable


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cards, container, false)
        cardsRecyclerView = view.findViewById(R.id.cards_recycler_view)
        addCreditCard = view.findViewById(R.id.add_credit_card)
        vaultView = view.findViewById(R.id.vault_view)
        vaultName = view.findViewById(R.id.vault_name)
        addCardButton = view.findViewById(R.id.add_card_btn)
        noCardsMessage = view.findViewById(R.id.no_items_view)
        cardholderNameET = view.findViewById(R.id.cardholder_name_et)
        cardNumberET = view.findViewById(R.id.card_number_et)
        expirationDateET = view.findViewById(R.id.expiration_date_et)
        cardNicknameET = view.findViewById(R.id.card_nickname_et)
        vaultET = view.findViewById(R.id.vault_et)
        cvvET = view.findViewById(R.id.cvv_et)
        cardNumberET.addTextChangedListener(FourDigitCardFormatWatcher(null))

        inputLineBackground = cardNumberET.background
        cardStates = ArrayList()

        val stackLayoutManager = StackLayoutManager()
        cardsAdapter = CreditCardsRecyclerViewAdapter(activity, GateKeeperApplication.cards, this)
        cardsRecyclerView.adapter = cardsAdapter
        cardsRecyclerView.layoutManager = stackLayoutManager
        cardsRecyclerView.setHasFixedSize(true)
        stackLayoutManager.setItemChangedListener (object: StackLayoutManager.ItemChangedListener{
            override fun onItemChanged(position: Int) {
                activeCard = filtered[position]
                updateUI()
            }
        })
        cardsRecyclerView.addItemDecoration(LinePagerIndicatorDecoration())

        addCardButton.setOnClickListener { startActivity(Intent(activity, CreateCreditCardActivity::class.java)) }
        addCreditCard.setOnClickListener { createCard() }
        vaultView.setOnClickListener { changeVault() }
        expirationDateET.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) { showMonthYearPicker() } }
        vaultET.setOnFocusChangeListener { _, hasFocus -> if(hasFocus) showVaultSelectorPicker() }

        toggleBottomInputs(false)

        return view
    }

    private fun createCard() {
        val card = CreditCard( id = "-1",
            cardName = "",
            type = CardType.Unknown,
            number = "",
            expirationDate = "20/23",
            cvv = "",
            cardholderName = "",
            vaultId = VaultRepository.getLastActiveRealVault().id,
            accountId = AuthRepository.getUserID())

        filtered.add(0,card)
        updateUI()
        cardsRecyclerView.smoothScrollToPosition(0)
    }

    @SuppressLint("RestrictedApi")
    private fun updateUI() {
        vaultName.text = currentVault.name

        if (activeCard == null) { activeCard = filtered[0] }

        cardNicknameET.setText(activeCard?.cardName)
        cardholderNameET.setText(activeCard?.cardholderName)
        cardNumberET.setText(activeCard?.number)
        expirationDateET.setText(activeCard?.expirationDate)
        cvvET.setText(activeCard?.cvv)
        vaultET.setText(VaultRepository.getVaultByID(activeCard?.vaultId ?: "")?.name ?: "")

        noCardsMessage.visibility = if (filtered.size > 0) View.GONE else View.VISIBLE
        addCreditCard.visibility = if (filtered.size > 0) View.VISIBLE else View.GONE
    }

    private fun showVaultSelectorPicker() {
        vaultET.clearFocus()
        vaultET.hideKeyboard()

        val vaults = GateKeeperApplication.vaults
        val vaultNames = arrayOfNulls<String>(vaults.size)
        var selected = -1
        vaults.forEachIndexed { index, vault -> vaultNames[index] = vault.name }
        vaults.forEachIndexed { index, vault -> if(vaultET.text.toString() == vault.name) selected = index }


        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Select Card Vault")
        builder.setSingleChoiceItems(vaultNames, selected) { dialog, which ->
            vaultET.setText(vaultNames[which])
            vaultET.clearFocus()
            vaultET.hideKeyboard()

            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.setOnCancelListener {
            vaultET.clearFocus()
            vaultET.hideKeyboard()
        }
        dialog.show()
    }

    private fun showMonthYearPicker() {
        expirationDateET.clearFocus()
        expirationDateET.hideKeyboard()

        val builder = MonthPickerDialog.Builder(activity,
            MonthPickerDialog.OnDateSetListener { selectedMonth, selectedYear ->
                val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec")
                expirationDateET.setText("${months[selectedMonth]}/$selectedYear")
                expirationDateET.clearFocus()
                expirationDateET.hideKeyboard()
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
            expirationDateET.clearFocus()
            expirationDateET.hideKeyboard()
        }
        dialog.setOnDismissListener {
            expirationDateET.clearFocus()
            expirationDateET.hideKeyboard()
        }
        dialog.show()

    }

    override fun onCreditCardClicked(card: CreditCard) { }

    override fun onCreditCardEditButtonClicked(card: CreditCard, position: Int) {
        if (cardStates[position] == CARD_STATE_DONE) {
            cardStates[position] = CARD_STATE_EDITED
            cardsRecyclerView.addOnItemTouchListener(rvDisabler)
        } else {
            cardStates[position] = CARD_STATE_DONE
            cardsRecyclerView.removeOnItemTouchListener(rvDisabler)
        }

        toggleBottomInputs(cardStates[position] == CARD_STATE_EDITED)
        cardsAdapter.updateCards(filtered, cardStates)
    }

    private fun toggleBottomInputs(canEdit: Boolean) {
        cardholderNameET.background = if(canEdit) inputLineBackground else null
        cardNumberET.background = if(canEdit) inputLineBackground else null
        expirationDateET.background = if(canEdit) inputLineBackground else null
        cvvET.background = if(canEdit) inputLineBackground else null
        vaultET.background = if(canEdit) inputLineBackground else null
        cardNicknameET.background = if(canEdit) inputLineBackground else null

        cardNicknameET.isFocusable = canEdit
        cardholderNameET.isFocusable = canEdit
        cardNumberET.isFocusable = canEdit
        expirationDateET.isFocusable = canEdit
        cvvET.isFocusable = canEdit
        vaultET.isFocusable = canEdit
    }

    private fun changeVault() {
        val intent = Intent(activity, SelectVaultActivity::class.java)
        intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_ACTIVE_VAULT)
        intent.putExtra("vault_id",currentVault.id)
        startActivityForResult(intent, GateKeeperConstants.CHANGE_ACTIVE_VAULT_REQUEST_CODE)
    }

    override fun onResume() {
        super.onResume()
        currentVault = VaultRepository.getLastActiveVault()
        filtered = CreditCardRepository.filterCardsByVault(currentVault)
        cardStates.clear()
        for (card in filtered) { cardStates.add(CARD_STATE_DONE) }
        cardsAdapter.updateCards(filtered, cardStates)
        updateUI()
    }

}
