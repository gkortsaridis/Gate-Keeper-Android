package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Utils.dp
import gr.gkortsaridis.gatekeeper.Interfaces.LoginSelectListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository


class LoginsRecyclerViewAdapter(
    private val context: Context,
    private val logins: ArrayList<Login>,
    private val packageManager: PackageManager,
    private val listener: LoginSelectListener): RecyclerView.Adapter<LoginsRecyclerViewAdapter.LoginViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_login, parent, false)
        return LoginViewHolder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return logins.size
    }

    override fun onBindViewHolder(holder: LoginViewHolder, position: Int) {
        val loginItem = logins[position]
        holder.bindLogin(loginItem, position, context, packageManager, listener)
    }

    class LoginViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private var loginName: TextView? = null
        private var loginUsername: TextView? = null
        private var loginImage: ImageView? = null
        private var loginAction: ImageButton? = null
        private var loginIcon: CardView? = null
        private var loginInitial: TextView? = null
        private var view: View = v

        init {
            loginName = view.findViewById(R.id.login_name)
            loginUsername = view.findViewById(R.id.login_username)
            loginImage = view.findViewById(R.id.login_image)
            loginAction = view.findViewById(R.id.login_action)
            loginIcon = view.findViewById(R.id.login_icon)
            loginInitial = view.findViewById(R.id.login_icon_initial)
        }

        fun bindLogin(login: Login, position: Int, context: Context, packageManager: PackageManager, listener: LoginSelectListener){
            this.loginName?.text = login.name
            this.loginUsername?.text = login.username

            val app = LoginsRepository.getApplicationInfoByPackageName(login.url, packageManager)

            val appIcon = app?.loadIcon(packageManager)
            if (appIcon != null) {
                this.loginIcon?.visibility = View.GONE
                this.loginImage?.visibility = View.VISIBLE
                this.loginImage?.setImageDrawable(appIcon)
            }else{
                this.loginIcon?.visibility = View.VISIBLE
                this.loginImage?.visibility = View.GONE
                if (login.name.isNotEmpty()) {
                    this.loginInitial?.text = login.name[0].toString()
                }
            }

            if(position == 0) {
                this.view.setPadding(0,20.dp,0,0)
            }

            val action = DataRepository.loginClickAction
            if (action == LoginsRepository.LOGIN_CLICK_ACTION_OPEN) { this.loginAction?.setBackgroundResource(R.drawable.copy) }
            else { this.loginAction?.setBackgroundResource(R.drawable.writing) }

            this.view.setOnClickListener { listener.onLoginClicked(login) }
            this.loginAction?.setOnClickListener { listener.onLoginActionClicked(login) }
        }

    }
}