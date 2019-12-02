package gr.gkortsaridis.gatekeeper.Interfaces

import android.content.pm.ResolveInfo

interface ApplicationSelectListener {
    fun onApplicationSelected(app: ResolveInfo)
}