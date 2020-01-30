package gr.gkortsaridis.gatekeeper.Utils

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewDisabler : RecyclerView.OnItemTouchListener {

    //Enable Only click events. Not scroll events
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean { return true }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {  }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {  }
}