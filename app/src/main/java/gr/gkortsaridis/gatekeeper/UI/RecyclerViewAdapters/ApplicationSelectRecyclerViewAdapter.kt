package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Interfaces.ApplicationSelectListener
import gr.gkortsaridis.gatekeeper.R

class ApplicationSelectRecyclerViewAdapter(
    private val context: Context,
    private var apps: ArrayList<ResolveInfo?>,
    private val packageManager: PackageManager,
    private val listener: ApplicationSelectListener): RecyclerView.Adapter<ApplicationSelectRecyclerViewAdapter.AppViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_application, parent, false)
        return AppViewHolder( inflatedView )
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appItem = apps[position]
        holder.bindApp(appItem, packageManager, listener)
    }

    fun refreshDataSet(data: ArrayList<ResolveInfo>) {
        apps.clear()
        apps.addAll(data)
        notifyDataSetChanged()
    }

    class AppViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private var appName: TextView? = null
        private var appIcon: ImageView? = null
        private var appUrl: EditText? = null
        private var view: View = v

        init {
            appName = view.findViewById(R.id.application_name)
            appIcon = view.findViewById(R.id.application_icon)
            appUrl = view.findViewById(R.id.app_url_web)
        }


        fun bindApp(app: ResolveInfo?, packageManager: PackageManager, listener: ApplicationSelectListener){

            if (app != null) {
                this.appIcon?.setImageDrawable(app.loadIcon(packageManager))
                this.appName?.text = app.loadLabel(packageManager)
                this.appUrl?.visibility = View.GONE
                this.appName?.visibility = View.VISIBLE
            } else {
                this.appIcon?.setImageResource(R.drawable.web)

                this.appUrl?.visibility = View.VISIBLE
                this.appName?.visibility = View.GONE
            }
            this.view.setOnClickListener { listener.onApplicationSelected(app, this.appUrl?.text.toString()) }
        }

    }
}