package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.R.attr.bitmap
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.VaultColor
import gr.gkortsaridis.gatekeeper.Interfaces.LoginSelectListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.DataRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository
import gr.gkortsaridis.gatekeeper.Utils.GlideApp
import gr.gkortsaridis.gatekeeper.Utils.dp


class LoginsRecyclerViewAdapter(
    private val context: Context,
    private var logins: ArrayList<Login>,
    private val packageManager: PackageManager,
    private val listener: LoginSelectListener): RecyclerView.Adapter<LoginsRecyclerViewAdapter.LoginViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_login_right, parent, false)
        return LoginViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return logins.size
    }

    override fun onBindViewHolder(holder: LoginViewHolder, position: Int) {
        val loginItem = logins[position]
        holder.bindLogin(loginItem, position, context, packageManager, listener)
    }

    fun updateLogins(logins: ArrayList<Login>) {
        this.logins = logins
        notifyDataSetChanged()
    }

    class LoginViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private var loginName: TextView? = null
        private var loginUsername: TextView? = null
        private var loginImage: ImageView? = null
        private var loginAction: ImageButton? = null
        private var loginInitial: TextView? = null
        private var loginImgContainer: RelativeLayout? = null
        private var loginImgContainer2: View? = null
        private var loginBackground: View? = null
        private var view: View = v

        init {
            loginName = view.findViewById(R.id.login_name)
            loginUsername = view.findViewById(R.id.login_username)
            loginImage = view.findViewById(R.id.login_image)
            loginAction = view.findViewById(R.id.login_action)
            loginInitial = view.findViewById(R.id.login_icon_initial)
            loginImgContainer = view.findViewById(R.id.login_img_container)
            loginImgContainer2 = view.findViewById(R.id.login_img_container_2)
            loginBackground = view.findViewById(R.id.login_background)
        }

        fun bindLogin(login: Login, position: Int, context: Context, packageManager: PackageManager, listener: LoginSelectListener){
            this.loginName?.text = login.name
            this.loginUsername?.text = login.username

            val vault = VaultRepository.getVaultByID(login.vault_id)
            loginImgContainer2?.setBackgroundResource(vault?.getVaultColorResource() ?: R.color.colorPrimaryDark)
            loginImgContainer?.setBackgroundResource(vault?.getVaultColorResource() ?: R.color.colorPrimaryDark)

            var color = ""
            when (vault?.color) {
                VaultColor.Red -> { color = "e53935" }
                VaultColor.Green -> { color = "4caf50" }
                VaultColor.Blue -> { color = "1e88e5" }
                VaultColor.Yellow -> { color = "fdd835" }
                VaultColor.White -> { color = "ffffff" }
            }

            val app = LoginsRepository.getApplicationInfoByPackageName(login.url, packageManager)

            val appIcon = app?.loadIcon(packageManager)
            if (appIcon != null) {
                this.loginInitial?.visibility = View.GONE
                this.loginImage?.visibility = View.VISIBLE
                this.loginImage?.setImageDrawable(appIcon)
            } else {
                this.loginImage?.visibility = View.VISIBLE
                GlideApp.with(context)
                    .load("https://besticon-demo.herokuapp.com/icon?url="+login.url+"&size=30..100..150&fallback_icon_color="+color)
                    .placeholder(R.drawable.padlock)
                    .listener(object: RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean { return false }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            val bitmap = resource?.toBitmap(8.dp, 8.dp)!!
                            //colorImageBackground(context, bitmap)
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

        //Get Dominant Color from ImageView
        private fun colorImageBackground(context: Context, imageView: ImageView) {
            val bitmapDrawable = imageView.drawable
            val bitmap = bitmapDrawable?.toBitmap()!!
            Palette.from(bitmap).generate {
                loginImgContainer?.setBackgroundColor(it?.getDominantColor( context.resources.getColor(R.color.colorAccent))!!)
                loginImgContainer2?.setBackgroundColor(it?.getDominantColor( context.resources.getColor(R.color.colorAccent))!!)
            }
        }

    }
}