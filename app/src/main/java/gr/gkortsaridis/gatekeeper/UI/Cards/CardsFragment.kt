package gr.gkortsaridis.gatekeeper.UI.Cards


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
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
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardUpdateListener
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
    private lateinit var editActionsContainer: LinearLayout
    private lateinit var saveAction: LinearLayout
    private lateinit var cancelAction: LinearLayout

    private lateinit var currentVault: Vault

    private lateinit var filtered: ArrayList<CreditCard>
    private lateinit var cardStates: ArrayList<Int>
    private var activeCard : CreditCard? = null
    private val rvDisabler = RecyclerViewDisabler()
    private lateinit var inputLineBackground: Drawable
    private var isEditing = false

    private var cardNicknameInputType: Int = -1
    private var cardNumberInputType: Int = -1
    private var cardholderNameInputType: Int = -1
    private var cardExpirationInputType: Int = -1
    private var cardCVVInputType: Int = -1
    private var cardVaultInputType: Int = -1

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
        editActionsContainer = view.findViewById(R.id.edit_actions_container)
        saveAction = view.findViewById(R.id.card_save_action)
        cancelAction = view.findViewById(R.id.card_cancel_action)
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
        expirationDateET.setOnFocusChangeListener { _, hasFocus -> if (hasFocus && isEditing) { showMonthYearPicker() } }
        vaultET.setOnFocusChangeListener { _, hasFocus -> if(hasFocus && isEditing) showVaultSelectorPicker() }

        saveAction.setOnClickListener { endEditing(true) }
        cancelAction.setOnClickListener { endEditing(false) }

        cardNicknameInputType = cardNicknameET.inputType
        cardNumberInputType = cardNumberET.inputType
        cardholderNameInputType = cardholderNameET.inputType
        cardExpirationInputType = expirationDateET.inputType
        cardCVVInputType = cvvET.inputType
        cardVaultInputType = vaultET.inputType

        cardholderNameET.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                val tempCard = activeCard?.copy()
                if (tempCard != null) {
                    tempCard.cardholderName = s.toString()
                    val tempFiltered = filtered
                    tempFiltered.replaceAll { if (it.id == tempCard.id) tempCard else it }
                    cardsAdapter.updateCards(tempFiltered, cardStates)
                }
            }
        })

        cardNumberET.addTextChangedListener(FourDigitCardFormatWatcher(null))
        cardNumberET.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val tempCard = activeCard?.copy()
                if (tempCard != null) {
                    tempCard.number = s.toString()
                    val tempFiltered = filtered
                    tempFiltered.replaceAll { if (it.id == tempCard.id) tempCard else it }
                    cardsAdapter.updateCards(tempFiltered, cardStates)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }
        })

        cvvET.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val tempCard = activeCard?.copy()
                if (tempCard != null) {
                    tempCard.cvv = s.toString()
                    val tempFiltered = filtered
                    tempFiltered.replaceAll { if (it.id == tempCard.id) tempCard else it }
                    cardsAdapter.updateCards(tempFiltered, cardStates)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }
        })

        toggleBottomInputs(false)

        return view
    }

    private fun endEditing(save: Boolean) {
        val viewDialog = ViewDialog(activity)

        toggleBottomInputs(false)
        var position = -1
        filtered.forEachIndexed{ pos, card -> if (card.id == activeCard?.id) { position = pos } }
        if (position != -1) { onCreditCardEditButtonClicked(activeCard!!, position) }

        if (!save) {
            cardNicknameET.setText(activeCard?.cardName)
            cardNumberET.setText(activeCard?.number)
            cardholderNameET.setText(activeCard?.cardholderName)
            expirationDateET.setText(activeCard?.expirationDate)
            cvvET.setText(activeCard?.cvv)
            vaultET.setText(VaultRepository.getVaultByID(activeCard?.vaultId ?: "")?.name)
        } else if (allDataFilled() && dataDifferentFromCurrentlySaved()) {
            if (activeCard != null) {
                activeCard!!.cardName = cardNicknameET.text.toString()
                activeCard!!.number = cardNumberET.text.toString()
                activeCard!!.cardholderName = cardholderNameET.text.toString()
                activeCard!!.cvv = cvvET.text.toString()
                activeCard!!.type = CreditCardRepository.getCreditCardType(activeCard!!.number)
                activeCard!!.expirationDate = expirationDateET.text.toString()
                viewDialog.showDialog()

                CreditCardRepository.updateCreditCard(activeCard!!, object: CreditCardUpdateListener {
                    override fun onCardUpdated(card: CreditCard) {
                        GateKeeperApplication.cards.replaceAll { if (it.id == card.id) card else it }
                        viewDialog.hideDialog()

                        updateUI()
                    }
                })
            }
        } else {
            Toast.makeText(activity, "You cannot leave blank card fields", Toast.LENGTH_SHORT).show()
        }

    }

    private fun dataDifferentFromCurrentlySaved(): Boolean {
        return cardNumberET.text.toString() != activeCard?.number
                || cardholderNameET.text.toString() != activeCard?.cardholderName
                || cvvET.text.toString() != activeCard?.cvv
                || expirationDateET.text.toString() != activeCard?.expirationDate?.substring(0,2)
                || cardNicknameET.text.toString() != activeCard?.cardName
                || vaultET.text.toString() != VaultRepository.getVaultByID(activeCard?.vaultId ?: "")?.name
    }

    private fun allDataFilled(): Boolean {
        return cardNumberET.text.toString().trim() != ""
                && cardholderNameET.text.toString().trim() != ""
                && cvvET.text.toString().trim() != ""
                && expirationDateET.text.toString().trim() != ""
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

        if (activeCard == null) {
            activeCard = filtered[0]
        }
        vaultName.text = VaultRepository.getVaultByID(activeCard!!.vaultId)?.name

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
            cardsRecyclerView.setOnTouchListener { _, _-> false }
            cardsRecyclerView.removeOnItemTouchListener(rvDisabler)
        }

        toggleBottomInputs(cardStates[position] == CARD_STATE_EDITED)
        cardsAdapter.updateCards(filtered, cardStates)
    }

    @SuppressLint("RestrictedApi")
    private fun toggleBottomInputs(canEdit: Boolean) {
        cardholderNameET.background = if(canEdit) inputLineBackground else null
        cardNumberET.background = if(canEdit) inputLineBackground else null
        expirationDateET.background = if(canEdit) inputLineBackground else null
        cvvET.background = if(canEdit) inputLineBackground else null
        vaultET.background = if(canEdit) inputLineBackground else null
        cardNicknameET.background = if(canEdit) inputLineBackground else null

        editActionsContainer.visibility = if (canEdit) View.VISIBLE else View.GONE
        cardNicknameET.inputType = if (canEdit) cardNicknameInputType else InputType.TYPE_NULL
        cardNumberET.inputType = if (canEdit) cardNumberInputType else InputType.TYPE_NULL
        cardholderNameET.inputType = if (canEdit) cardholderNameInputType else InputType.TYPE_NULL
        expirationDateET.inputType = if (canEdit) cardExpirationInputType else InputType.TYPE_NULL
        cvvET.inputType = if (canEdit) cardCVVInputType else InputType.TYPE_NULL
        vaultET.inputType = if (canEdit) cardVaultInputType else InputType.TYPE_NULL

        addCreditCard.visibility = if (canEdit) View.GONE else View.VISIBLE
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
