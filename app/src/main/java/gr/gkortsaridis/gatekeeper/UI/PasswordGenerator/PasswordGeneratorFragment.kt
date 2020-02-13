package gr.gkortsaridis.gatekeeper.UI.PasswordGenerator


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Utils.PasswordGenerator

class PasswordGeneratorFragment : Fragment() {

    private lateinit var lettersSw: Switch
    private lateinit var capitalsSw: Switch
    private lateinit var numbersSw: Switch
    private lateinit var symbolsSw: Switch
    private lateinit var result: TextView
    private lateinit var refresh: ImageButton
    private lateinit var hide: ImageButton
    private lateinit var lengthSegmented: RadioGroup
    private lateinit var adView: AdView

    private var hasLetters = true
    private var hasCapitals = true
    private var hasNumbers = true
    private var hasSymbols = true
    private var password = ""
    private var length = 8
    private var passwordVisible = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_password_generator, container, false)

        lettersSw = view.findViewById(R.id.switch_letters)
        capitalsSw = view.findViewById(R.id.switch_capitals)
        numbersSw = view.findViewById(R.id.switch_numbers)
        symbolsSw = view.findViewById(R.id.switch_symbols)
        result = view.findViewById(R.id.result_tv)
        refresh = view.findViewById(R.id.refresh_btn)
        hide = view.findViewById(R.id.hide_btn)
        lengthSegmented = view.findViewById(R.id.length_segmented)
        adView = view.findViewById(R.id.adview)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        lettersSw.isChecked = hasLetters
        capitalsSw.isChecked = hasCapitals
        numbersSw.isChecked = hasNumbers
        symbolsSw.isChecked = hasSymbols

        lettersSw.setOnCheckedChangeListener {_, isChecked ->
            hasLetters = isChecked
            generatePassAndDisplay()
            lockFinalSwitch()
        }
        capitalsSw.setOnCheckedChangeListener {_, isChecked ->
            hasCapitals = isChecked
            generatePassAndDisplay()
            lockFinalSwitch()
        }
        numbersSw.setOnCheckedChangeListener {_, isChecked ->
            hasNumbers = isChecked
            generatePassAndDisplay()
            lockFinalSwitch()
        }
        symbolsSw.setOnCheckedChangeListener {_, isChecked ->
            hasSymbols = isChecked
            generatePassAndDisplay()
            lockFinalSwitch()
        }

        lengthSegmented.check(R.id.len_8)
        lengthSegmented.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.len_8 -> {
                    length = 8
                    generatePassAndDisplay()
                }
                R.id.len_16 -> {
                    length = 16
                    generatePassAndDisplay()
                }
                R.id.len_24 -> {
                    length = 24
                    generatePassAndDisplay()
                }
                R.id.len_32 -> {
                    length = 32
                    generatePassAndDisplay()
                }
                R.id.len_48 -> {
                    length = 48
                    generatePassAndDisplay()
                }
                R.id.len_56 -> {
                    length = 56
                    generatePassAndDisplay()
                }
            }
        }

        refresh.setOnClickListener { generatePassAndDisplay() }
        hide.setOnClickListener {
            passwordVisible = !passwordVisible
            hide.background = if (passwordVisible) resources.getDrawable(R.drawable.eye) else resources.getDrawable(R.drawable.hide)
            displayPassword()
        }

        generatePassAndDisplay()

        return view
    }

    private fun lockFinalSwitch() {
        lettersSw.isEnabled = !(hasLetters && !hasCapitals && !hasNumbers && !hasSymbols)
        capitalsSw.isEnabled = !(hasCapitals && !hasLetters && !hasNumbers && !hasSymbols)
        numbersSw.isEnabled = !(hasNumbers && !hasLetters && !hasCapitals && !hasSymbols)
        symbolsSw.isEnabled = !(hasSymbols && !hasLetters && !hasCapitals && !hasNumbers)
    }

    private fun generatePassAndDisplay() {
        password = PasswordGenerator.randomString(length, hasLetters, hasCapitals, hasNumbers, hasSymbols)
        displayPassword()
    }

    private fun displayPassword() {
        if (passwordVisible) {
            result.text = password
        } else {
            var hiddenPart = ""
            for (i in 5..length) { hiddenPart += "*" }
            result.text = password.substring(0,4)+hiddenPart
        }
    }


}
