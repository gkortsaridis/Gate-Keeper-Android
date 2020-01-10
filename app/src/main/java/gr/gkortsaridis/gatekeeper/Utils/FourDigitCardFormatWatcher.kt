package gr.gkortsaridis.gatekeeper.Utils

import android.text.TextUtils
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import gr.gkortsaridis.gatekeeper.Entities.CardType
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.CreditCardRepository

class FourDigitCardFormatWatcher(private val cardType : ImageView) : TextWatcher {

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (s.isNotEmpty()) {
            // Remove spacing char
            if (s.length % 5 == 0) {
                val c = s[s.length - 1]
                if (space == c.toString()) {
                    s.delete(s.length - 1, s.length)
                }
            }
            // Insert char where needed.
            if (s.length % 5 == 0) {
                val c = s[s.length - 1]
                // Only if its a digit where there should be a space we insert a space
                if (Character.isDigit(c) && TextUtils.split(s.toString(), space).size <= 3) {
                    s.insert(s.length - 1, space)
                }
            }

            //Check Card Type
            when (CreditCardRepository.getCreditCardType(s.toString())) {
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
                null -> this.cardType.visibility = View.INVISIBLE
            }

        }


    }

    companion object {
        // Change this to what you want... ' ', '-' etc..
        private const val space = " "
    }
}