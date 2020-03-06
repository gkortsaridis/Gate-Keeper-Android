package gr.gkortsaridis.gatekeeper.UI.PasswordGenerator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Utils.PasswordGenerator
import gr.gkortsaridis.gatekeeper.Utils.dp
import io.noties.tumbleweed.Timeline
import io.noties.tumbleweed.Tween
import io.noties.tumbleweed.android.ViewTweenManager
import io.noties.tumbleweed.android.types.Alpha
import io.noties.tumbleweed.android.types.Translation
import io.noties.tumbleweed.equations.Cubic
import kotlinx.android.synthetic.main.fragment_password_generator.*

class PasswordGeneratorFragment : Fragment() {

    private var hasLetters = true
    private var hasCapitals = true
    private var hasNumbers = true
    private var hasSymbols = true
    private var password = ""
    private var length = 8
    private var passwordVisible = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_password_generator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adRequest = AdRequest.Builder().build()
        adview.loadAd(adRequest)
        animateAdContainerIn()

        switch_letters.isChecked = hasLetters
        switch_capitals.isChecked = hasCapitals
        switch_numbers.isChecked = hasNumbers
        switch_symbols.isChecked = hasSymbols

        switch_letters.setOnCheckedChangeListener {_, isChecked ->
            hasLetters = isChecked
            generatePassAndDisplay()
            lockFinalSwitch()
        }
        switch_capitals.setOnCheckedChangeListener {_, isChecked ->
            hasCapitals = isChecked
            generatePassAndDisplay()
            lockFinalSwitch()
        }
        switch_numbers.setOnCheckedChangeListener {_, isChecked ->
            hasNumbers = isChecked
            generatePassAndDisplay()
            lockFinalSwitch()
        }
        switch_symbols.setOnCheckedChangeListener {_, isChecked ->
            hasSymbols = isChecked
            generatePassAndDisplay()
            lockFinalSwitch()
        }

        length_segmented.check(R.id.len_8)
        length_segmented.setOnCheckedChangeListener { _, checkedId ->
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

        refresh_btn.setOnClickListener { generatePassAndDisplay() }
        hide_btn.setOnClickListener {
            passwordVisible = !passwordVisible
            hide_btn.background = if (passwordVisible) resources.getDrawable(R.drawable.eye) else resources.getDrawable(R.drawable.hide)
            displayPassword()
        }

        generatePassAndDisplay()

    }

    private fun lockFinalSwitch() {
        switch_letters.isEnabled = !(hasLetters && !hasCapitals && !hasNumbers && !hasSymbols)
        switch_capitals.isEnabled = !(hasCapitals && !hasLetters && !hasNumbers && !hasSymbols)
        switch_numbers.isEnabled = !(hasNumbers && !hasLetters && !hasCapitals && !hasSymbols)
        switch_symbols.isEnabled = !(hasSymbols && !hasLetters && !hasCapitals && !hasNumbers)
    }

    private fun generatePassAndDisplay() {
        password = PasswordGenerator.randomString(length, hasLetters, hasCapitals, hasNumbers, hasSymbols)
        displayPassword()
    }

    private fun displayPassword() {
        if (passwordVisible) {
            result_tv.text = password
        } else {
            var hiddenPart = ""
            for (i in 5..length) { hiddenPart += "*" }
            result_tv.text = password.substring(0,4)+hiddenPart
        }
    }

    private fun animateAdContainerIn() {
        Timeline.createParallel()
            .push(Tween.to(adview_container, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(adview_container, Translation.XY).target(0f,-90.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(adview_container))
    }
}
