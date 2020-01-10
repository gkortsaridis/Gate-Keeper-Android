package gr.gkortsaridis.gatekeeper.UI.Cards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.Timestamp
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardCreateListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.CreditCardRepository

class CreateCreditCardActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var cardholderName: EditText
    private lateinit var cvv: EditText
    private lateinit var expiryMonth: EditText
    private lateinit var expiryYear: EditText
    private lateinit var cardNumber: EditText

    private lateinit var card: CreditCard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_credit_card)

        val cardId = intent.getStringExtra("card_id") ?: "-1"
        if (cardId != "-1") {
            card = CreditCardRepository.getCreditCardById(cardId)!!
        }else {
            card = CreditCard(
                id="-1",
                cardName = "",
                type = CardType.Visa,
                number = "",
                expirationDate = "     ",
                cvv = "",
                cardholderName = "",
                accountId = AuthRepository.getUserID())
        }

        toolbar = findViewById(R.id.toolbar)
        cardholderName = findViewById(R.id.cardholder_name)
        cvv = findViewById(R.id.cvv)
        expiryMonth = findViewById(R.id.expire_month)
        expiryYear = findViewById(R.id.expire_year)
        cardNumber = findViewById(R.id.card_number)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = if (cardId == "-1") "Add new Credit Card" else "Edit Credit Card"

        cardNumber.setText(card.number)
        cardholderName.setText(card.cardholderName)
        cvv.setText(card.cvv)
        val month = card.expirationDate.substring(0,2)
        val year = card.expirationDate.substring(3,5)
        expiryMonth.setText(month)
        expiryYear.setText(year)
    }

    override fun onBackPressed() {
        val viewDialog = ViewDialog(this)

        if (card.id == "-1") {
            if (cardNumber.text.toString().trim() != ""
                && cardholderName.text.toString().trim() != ""
                && cvv.text.toString().trim() != ""
                && expiryMonth.text.toString().trim() != ""
                && expiryYear.text.toString().trim() != "") {
                //Create Credit Card
                card.number = cardNumber.text.toString()
                card.cardholderName = cardholderName.text.toString()
                card.cvv = cvv.text.toString()
                card.expirationDate = expiryMonth.text.toString()+"/"+expiryYear.text.toString()

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
            if (cardNumber.text.toString() != card.number
                && cardholderName.text.toString() != card.cardholderName
                && cvv.text.toString() != card.cvv
                && expiryMonth.text.toString() != card.expirationDate.substring(0,2)
                && expiryYear.text.toString().trim() != card.expirationDate.substring(3,5)) {
                card.number = cardNumber.text.toString()
                card.cardholderName = cardholderName.text.toString()
                card.cvv = cvv.text.toString()
                card.expirationDate = expiryMonth.text.toString()+"/"+expiryYear.text.toString()

                //Add to local arraylist
                //go back
            }else {
                Toast.makeText(this, "Incomplete card data", Toast.LENGTH_SHORT).show()
            }
        }

        super.onBackPressed()

    }
}
