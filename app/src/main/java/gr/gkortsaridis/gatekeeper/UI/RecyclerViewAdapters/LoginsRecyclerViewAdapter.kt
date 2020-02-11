package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Utils.dp
import gr.gkortsaridis.gatekeeper.Interfaces.LoginSelectListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.Utils.GlideApp


class LoginsRecyclerViewAdapter(
    private val context: Context,
    private val logins: ArrayList<Login>,
    private val packageManager: PackageManager,
    private val listener: LoginSelectListener): RecyclerView.Adapter<LoginsRecyclerViewAdapter.LoginViewHolder>() {

    private val RIGHT_SIDE = 0
    private val LEFT_SIDE = 1

    override fun getItemViewType(position: Int): Int {
        return RIGHT_SIDE
        //return if (position % 2 == 0) RIGHT_SIDE else LEFT_SIDE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginViewHolder {
        val inflatedView =
            if (viewType == LEFT_SIDE) LayoutInflater.from(context).inflate(R.layout.recycler_view_item_login_left, parent, false)
            else LayoutInflater.from(context).inflate(R.layout.recycler_view_item_login_right, parent, false)
        return LoginViewHolder(inflatedView)
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
        private var loginInitial: TextView? = null
        private var view: View = v

        init {
            loginName = view.findViewById(R.id.login_name)
            loginUsername = view.findViewById(R.id.login_username)
            loginImage = view.findViewById(R.id.login_image)
            loginAction = view.findViewById(R.id.login_action)
            loginInitial = view.findViewById(R.id.login_icon_initial)
        }

        fun bindLogin(login: Login, position: Int, context: Context, packageManager: PackageManager, listener: LoginSelectListener){
            this.loginName?.text = login.name
            this.loginUsername?.text = login.username

            val app = LoginsRepository.getApplicationInfoByPackageName(login.url, packageManager)

            val appIcon = app?.loadIcon(packageManager)
            if (appIcon != null) {
                this.loginInitial?.visibility = View.GONE
                this.loginImage?.visibility = View.VISIBLE
                this.loginImage?.setImageDrawable(appIcon)
            } else {
                this.loginImage?.visibility = View.VISIBLE
                GlideApp.with(context)
                    .load("https://besticon-demo.herokuapp.com/icon?url="+login.url+"&size=80..120..200")
                    .placeholder(R.drawable.camera)
                    .listener(object: RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            loginInitial?.visibility = View.VISIBLE
                            loginImage?.visibility = View.GONE
                            if (login.name.isNotEmpty()) {
                                if (login.name.length > 1) {
                                    loginInitial?.text = login.name[0].toString().toUpperCase() + login.name[0].toString().toUpperCase()
                                }else {
                                    loginInitial?.text = login.name[0].toString().toUpperCase()
                                }
                            }
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .into(loginImage!!)
            }

            val action = DataRepository.loginClickAction
            if (action == LoginsRepository.LOGIN_CLICK_ACTION_OPEN) { this.loginAction?.setBackgroundResource(R.drawable.copy) }
            else { this.loginAction?.setBackgroundResource(R.drawable.writing) }

            this.view.setOnClickListener { listener.onLoginClicked(login) }
            this.loginAction?.setOnClickListener { listener.onLoginActionClicked(login) }
        }

    }
}