package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Entities.dp
import gr.gkortsaridis.gatekeeper.Interfaces.LoginSelectListener

class LoginsRecyclerViewAdapter(
    private val context: Context,
    private val logins: ArrayList<Login>,
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
        holder.bindLogin(loginItem, position, listener)
    }

    class LoginViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private var loginName: TextView? = null
        private var loginUsername: TextView? = null
        private var view: View = v

        init {
            loginName = view.findViewById(R.id.login_name)
            loginUsername = view.findViewById(R.id.login_username)
        }

        fun bindLogin(login: Login, position: Int, listener: LoginSelectListener){
            this.loginName?.text = login.name
            this.loginUsername?.text = login.username

            if(position == 0) {
                this.view.setPadding(0,20.dp,0,0)
            }

            this.view.setOnClickListener { listener.onLoginClicked(login) }
        }

    }
}