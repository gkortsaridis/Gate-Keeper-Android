package gr.gkortsaridis.gatekeeper.Utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

val Int.px: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}