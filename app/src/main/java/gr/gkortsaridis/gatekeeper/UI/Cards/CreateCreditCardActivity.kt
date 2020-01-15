package gr.gkortsaridis.gatekeeper.UI.Cards

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.UI.Vaults.SelectVaultActivity
import gr.gkortsaridis.gatekeeper.Utils.FourDigitCardFormatWatcher
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperConstants.CHANGE_VAULT_REQUEST_CODE

class CreateCreditCardActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var cardholderName: EditText
    private lateinit var cvv: EditText
    private lateinit var expiryMonth: EditText
    private lateinit var expiryYear: EditText
    private lateinit var cardNumber: EditText
    private lateinit var cardType: ImageView
    private lateinit var cardNickname: EditText
    private lateinit var cardSave: Button
    private lateinit var vaultView: LinearLayout
    private lateinit var vaultName: TextView

    private lateinit var card: CreditCard
    private lateinit var vaultToAdd: Vault

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_credit_card)

        val cardId = intent.getStringExtra("card_id") ?: "-1"
        if (cardId != "-1") {
            card = CreditCardRepository.getCreditCardById(cardId)!!
            vaultToAdd = VaultRepository.getVaultByID(card.vaultId)!!
        }else {
            vaultToAdd = VaultRepository.getLastActiveRealVault()
            card = CreditCard(
                id="-1",
                cardName = "",
                type = CardType.Visa,
                number = "",
                expirationDate = "     ",
                cvv = "",
                cardholderName = "",
                vaultId = vaultToAdd.id,
                accountId = AuthRepository.getUserID())
        }

        toolbar = findViewById(R.id.toolbar)
        cardholderName = findViewById(R.id.cardholder_name)
        cvv = findViewById(R.id.cvv)
        expiryMonth = findViewById(R.id.expire_month)
        expiryYear = findViewById(R.id.expire_year)
        cardNumber = findViewById(R.id.card_number)
        cardType = findViewById(R.id.card_type)
        cardNickname = findViewById(R.id.card_nickname)
        cardSave = findViewById(R.id.card_save)
        vaultView = findViewById(R.id.vault_view)
        vaultName = findViewById(R.id.vault_name)
        cardNumber.addTextChangedListener(FourDigitCardFormatWatcher(cardType))

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = if (cardId == "-1") "Add new Credit Card" else "Edit Credit Card"

        cardSave.setOnClickListener { saveCreditCard() }
        vaultView.setOnClickListener { changeVault() }

        updateUI()
    }

    private fun updateUI() {
        cardNickname.setText(card.cardName)
        cardNumber.setText(card.number)
        cardholderName.setText(card.cardholderName)
        cvv.setText(card.cvv)
        val month = card.expirationDate.substring(0,2)
        val year = card.expirationDate.substring(3,5)
        expiryMonth.setText(month)
        expiryYear.setText(year)
        vaultName.text = vaultToAdd.name
        setCardType()
    }

    private fun changeVault() {
        val intent = Intent(this, SelectVaultActivity::class.java)
        intent.putExtra("action", GateKeeperConstants.ACTION_CHANGE_VAULT)
        intent.putExtra("vault_id",vaultToAdd.id)
        startActivityForResult(intent, CHANGE_VAULT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHANGE_VAULT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val vaultId = data!!.data.toString()
            vaultToAdd = VaultRepository.getVaultByID(vaultId)!!
            VaultRepository.setActiveVault(vaultToAdd)
            updateUI()
        }

    }

    private fun saveCreditCard() {
        val viewDialog = ViewDialog(this)

        if (card.id == "-1") {
            if (allDataFilled()) {
                //Create Credit Card
                card.cardName = cardNickname.text.toString()
                card.number = cardNumber.text.toString()
                card.cardholderName = cardholderName.text.toString()
                card.cvv = cvv.text.toString()
                card.type = CreditCardRepository.getCreditCardType(card.number)
                card.expirationDate = expiryMonth.text.toString()+"/"+expiryYear.text.toString()
                card.vaultId = vaultToAdd.id

                viewDialog.showDialog()
                CreditCardRepository.encryptAndStoreCard(this, card, object: CreditCardCreateListener{
                    override fun onCreditCardCreated(card: CreditCard) {
                        viewDialog.hideDialog()
                        GateKeeperApplication.cards.add(card)
                        finish()
                    }

                    override fun onCreditCardCreateError() { }
                })
            }else {
                Toast.makeText(this, "Incomplete card data", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (allDataFilled() && dataDifferentFromCurrentlySaved()) {
                card.cardName = cardNickname.text.toString()
                card.number = cardNumber.text.toString()
                card.cardholderName = cardholderName.text.toString()
                card.cvv = cvv.text.toString()
                card.type = CreditCardRepository.getCreditCardType(card.number)
                card.expirationDate = expiryMonth.text.toString()+"/"+expiryYear.text.toString()
                card.vaultId = vaultToAdd.id
                viewDialog.showDialog()

                CreditCardRepository.updateCreditCard(card, object: CreditCardUpdateListener {
                    override fun onCardUpdated(card: CreditCard) {
                        GateKeeperApplication.cards.replaceAll { if (it.id == card.id) card else it }
                        viewDialog.hideDialog()
                        finish()
                    }
                })
            }
        }

    }

    private fun setCardType() {
        when (CreditCardRepository.getCreditCardType(cardNumber.text.toString())) {
            CardType.Visa -> {
                this.cardType.visibility = View.VISIBLE
                this.cardType.setImageResource(R.drawable.visa)
            }
            CardType.Mastercard -> {
                this.cardType.visibility = View.VISIBLE
                this.cardType.setImageResource(R.drawable.mastercard)
            }
            CardType.DiscoverCard -> {
                this.cardType.visibility = View.VISIBLE
                this.cardType.setImageResource(R.drawable.discover)
            }
            CardType.DinersClub -> {
                this.cardType.visibility = View.VISIBLE
                this.cardType.setImageResource(R.drawable.discover)
            }
            CardType.Amex -> {
                this.cardType.visibility = View.VISIBLE
                this.cardType.setImageResource(R.drawable.amex)
            }
            CardType.Unknown -> this.cardType.visibility = View.INVISIBLE
        }

    }

    private fun allDataFilled(): Boolean {
        return cardNumber.text.toString().trim() != ""
                && cardholderName.text.toString().trim() != ""
                && cvv.text.toString().trim() != ""
                && expiryMonth.text.toString().trim() != ""
                && expiryYear.text.toString().trim() != ""
                && cardNickname.text.toString().trim() != ""
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (card.id != "-1") { menuInflater.inflate(R.menu.delete_item_menu, menu) }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) { deleteCard() }
        else if (item.itemId == android.R.id.home) {
            if (card.id != "-1" && dataDifferentFromCurrentlySaved()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Save card")
                builder.setMessage("There seem to be some changes. Would you like to save them?")
                builder.setPositiveButton("YES"){_, _ -> saveCreditCard() }
                builder.setNegativeButton("No"){_, _ -> finish() }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }else {
                finish()
            }
        }
        return true
    }

    private fun deleteCard() {
        CreditCardRepository.deleteCreditCard(card, object : CreditCardDeleteListener {
            override fun onCardDeleted() {
                GateKeeperApplication.cards.remove(card)
                finish()
            }
        })
    }

    private fun dataDifferentFromCurrentlySaved(): Boolean {
        return cardNumber.text.toString() != card.number
                || cardholderName.text.toString() != card.cardholderName
                || cvv.text.toString() != card.cvv
                || expiryMonth.text.toString() != card.expirationDate.substring(0,2)
                || expiryYear.text.toString() != card.expirationDate.substring(3,5)
                || cardNickname.text.toString() != card.cardName
                || card.vaultId != vaultToAdd.id
    }

    override fun onBackPressed() {
        if (card.id != "-1" && dataDifferentFromCurrentlySaved()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Save card")
            builder.setMessage("There seem to be some changes. Would you like to save them?")
            builder.setPositiveButton("YES"){_, _ -> saveCreditCard() }
            builder.setNegativeButton("No"){_, _ -> super.onBackPressed() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }else {
            super.onBackPressed()
        }
    }
}
