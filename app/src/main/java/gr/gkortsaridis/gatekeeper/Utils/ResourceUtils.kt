package gr.gkortsaridis.gatekeeper.Utils

import android.content.res.Resources

val Int.px: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()