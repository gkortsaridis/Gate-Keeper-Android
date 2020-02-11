package gr.gkortsaridis.gatekeeper.UI

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.florent37.shapeofview.shapes.RoundRectView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.UI.About.AboutFragment
import gr.gkortsaridis.gatekeeper.UI.Account.MyAccountFragment
import gr.gkortsaridis.gatekeeper.UI.Authentication.AuthenticationBaseActivity
import gr.gkortsaridis.gatekeeper.UI.Cards.CardsFragment
import gr.gkortsaridis.gatekeeper.UI.Devices.DevicesFragment
import gr.gkortsaridis.gatekeeper.UI.Logins.LoginsFragment
import gr.gkortsaridis.gatekeeper.UI.Notes.NotesFragment
import gr.gkortsaridis.gatekeeper.UI.Settings.SettingsFragment
import gr.gkortsaridis.gatekeeper.Utils.GlideApp
import gr.gkortsaridis.gatekeeper.Utils.dp
import io.noties.tumbleweed.Timeline
import io.noties.tumbleweed.Tween
import io.noties.tumbleweed.android.ViewTweenManager
import io.noties.tumbleweed.android.types.Alpha
import io.noties.tumbleweed.android.types.Translation


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var contentFrame: FrameLayout

    private lateinit var navName: TextView
    private lateinit var navContainerPasswords: RelativeLayout
    private lateinit var navContainerCards: RelativeLayout
    private lateinit var navContainerNotes: RelativeLayout
    private lateinit var navContainerDevices: RelativeLayout
    private lateinit var navContainerAccount: RelativeLayout
    private lateinit var navButtonSettings: ImageView
    private lateinit var navButtonAbout: ImageView
    private lateinit var navButtonLogout: ImageView
    private lateinit var navTextPasswords: TextView
    private lateinit var navTextCards: TextView
    private lateinit var navTextNotes: TextView
    private lateinit var navTextAccount: TextView
    private lateinit var navTextDevices: TextView
    private lateinit var profileImage: ImageView
    private lateinit var passwordsRoundRect: RoundRectView
    private lateinit var cardsRoundRect: RoundRectView
    private lateinit var notesRoundRect: RoundRectView
    private lateinit var accountRoundRect: RoundRectView
    private lateinit var devicesRoundRect: RoundRectView
    private lateinit var youAreSecuredTV: TextView

    private lateinit var loginsFragment: LoginsFragment
    private lateinit var cardsFragment: CardsFragment
    private lateinit var notesFragment: NotesFragment
    private lateinit var accountFragment: MyAccountFragment
    private lateinit var devicesFragment: DevicesFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginsFragment = LoginsFragment()
        cardsFragment = CardsFragment(this)
        notesFragment = NotesFragment(this)
        accountFragment = MyAccountFragment(this)
        devicesFragment = DevicesFragment(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        contentFrame = findViewById(R.id.content_frame)
        drawer = findViewById(R.id.drawer_layout)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        drawer.addDrawerListener(object: DrawerLayout.SimpleDrawerListener(){
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                if (newState == DrawerLayout.STATE_SETTLING && !drawer.isDrawerOpen(GravityCompat.START)) {

                    val animationLength = 0.25f
                    val returnAnimationLength = 0.25f
                    val animationDistance = 20.dp.toFloat()
                    val transition = io.noties.tumbleweed.equations.Cubic.IN

                    youAreSecuredTV.alpha = 0f

                    Timeline.createSequence()
                        .push(
                            Timeline.createParallel()
                                .push(
                                    Timeline.createSequence()
                                        .push(Tween.to(navTextPasswords, Translation.XY, animationLength).target(animationDistance, 0f).ease(transition))
                                        .push(Tween.to(navTextPasswords, Translation.XY, returnAnimationLength).target(0f, 0f).ease(transition))
                                )
                                .push(
                                    Timeline.createSequence()
                                        .pushPause(0.1f)
                                        .push(Tween.to(navTextCards, Translation.XY, animationLength).target(animationDistance, 0f).ease(transition))
                                        .push(Tween.to(navTextCards, Translation.XY, returnAnimationLength).target(0f, 0f).ease(transition))
                                )
                                .push(
                                    Timeline.createSequence()
                                        .pushPause(0.2f)
                                        .push(Tween.to(navTextNotes, Translation.XY, animationLength).target(animationDistance, 0f).ease(transition))
                                        .push(Tween.to(navTextNotes, Translation.XY, returnAnimationLength).target(0f, 0f).ease(transition))
                                )
                                .push(
                                    Timeline.createSequence()
                                        .pushPause(0.3f)
                                        .push(Tween.to(navTextAccount, Translation.XY, animationLength).target(animationDistance, 0f).ease(transition))
                                        .push(Tween.to(navTextAccount, Translation.XY, returnAnimationLength).target(0f, 0f).ease(transition))
                                )
                                .push(
                                    Timeline.createSequence()
                                        .pushPause(0.4f)
                                        .push(Tween.to(navTextDevices, Translation.XY, animationLength).target(animationDistance, 0f).ease(transition))
                                        .push(Tween.to(navTextDevices, Translation.XY, returnAnimationLength).target(0f, 0f).ease(transition))
                                )
                        )
                        .push(
                            Tween.to(youAreSecuredTV, Alpha.VIEW, 1.0f).target(1.0f)
                        )
                        .start(ViewTweenManager.get(passwordsRoundRect))
                }
            }
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        navName = findViewById(R.id.nav_name)
        navContainerPasswords = findViewById(R.id.nav_container_passwords)
        navContainerCards = findViewById(R.id.nav_container_cards)
        navContainerNotes = findViewById(R.id.nav_container_notes)
        navContainerAccount = findViewById(R.id.nav_container_account)
        navContainerDevices = findViewById(R.id.nav_container_devices)
        navButtonSettings = findViewById(R.id.nav_button_settings)
        navButtonAbout = findViewById(R.id.nav_button_about)
        navButtonLogout = findViewById(R.id.nav_button_logout)
        navTextPasswords = findViewById(R.id.nav_text_passwords)
        navTextCards = findViewById(R.id.nav_text_cards)
        navTextNotes = findViewById(R.id.nav_text_notes)
        navTextDevices = findViewById(R.id.nav_text_devices)
        navTextAccount = findViewById(R.id.nav_text_account)
        profileImage = findViewById(R.id.profile_image)
        passwordsRoundRect = findViewById(R.id.passwords_rectview)
        cardsRoundRect = findViewById(R.id.cards_rectview)
        notesRoundRect = findViewById(R.id.notes_rectview)
        accountRoundRect = findViewById(R.id.account_rectview)
        devicesRoundRect = findViewById(R.id.devices_rectview)
        youAreSecuredTV = findViewById(R.id.your_are_secured_tv)

        navContainerPasswords.setOnClickListener { switchFragment("Passwords") }
        navContainerCards.setOnClickListener { switchFragment("Cards") }
        navContainerNotes.setOnClickListener { switchFragment("Notes") }
        navContainerDevices.setOnClickListener { switchFragment("Devices") }
        navContainerAccount.setOnClickListener { switchFragment("Account") }
        navButtonSettings.setOnClickListener { switchFragment("Settings") }
        navButtonAbout.setOnClickListener { switchFragment("About") }
        navButtonLogout.setOnClickListener { switchFragment("Logout") }


        GlideApp
            .with(this)
            .load(getUserImageReference())
            .placeholder(R.drawable.camera)
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    profileImage.setImageResource(R.mipmap.ic_launcher_round)
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }
            })
            .into(profileImage)

        val userName = GateKeeperApplication.user?.displayName ?: GateKeeperApplication.user?.email ?: ""
        navName.text = userName
        switchFragment("Passwords")
    }

    private fun getUserImageReference(): StorageReference {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("userImages")
        return imagesRef.child(AuthRepository.getUserID()+".jpg")
    }

    private fun displayFragment(fragment: Fragment?){
        if (fragment != null) {
            supportFragmentManager.beginTransaction().replace(contentFrame.id, fragment, "ContentFragment").commit()
        }
    }

    private fun logout() {
        val intent = Intent(this, AuthenticationBaseActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun switchFragment(what: String) {
        var fragmentToReplace: Fragment? = null

        navTextPasswords.typeface = Typeface.DEFAULT
        navTextNotes.typeface = Typeface.DEFAULT
        navTextCards.typeface = Typeface.DEFAULT
        navTextDevices.typeface = Typeface.DEFAULT
        navTextAccount.typeface = Typeface.DEFAULT
        navContainerPasswords.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        navContainerCards.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        navContainerNotes.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        navContainerDevices.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        navContainerAccount.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        navButtonSettings.setBackgroundColor(resources.getColor(android.R.color.transparent))
        navButtonAbout.setBackgroundColor(resources.getColor(android.R.color.transparent))
        navButtonLogout.setBackgroundColor(resources.getColor(android.R.color.transparent))
        navButtonSettings.setImageResource(R.drawable.settings)
        navButtonAbout.setImageResource(R.drawable.copyright)

        when (what) {
            "Passwords" -> {
                fragmentToReplace = LoginsFragment()
                supportActionBar?.title = "GateKeeper Passwords"
                navTextPasswords.typeface = Typeface.DEFAULT_BOLD
                navContainerPasswords.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                //loginsFragment.animateFabIn()
            }
            "Cards" -> {
                fragmentToReplace = cardsFragment
                supportActionBar?.title = "GateKeeper Cards"
                navTextCards.typeface = Typeface.DEFAULT_BOLD
                navContainerCards.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
            "Notes" -> {
                fragmentToReplace = notesFragment
                supportActionBar?.title = "GateKeeper Notes"
                navTextNotes.typeface = Typeface.DEFAULT_BOLD
                navContainerNotes.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
            "Account" -> {
                fragmentToReplace = accountFragment
                supportActionBar?.title = "My GateKeeper Account"
                navTextAccount.typeface = Typeface.DEFAULT_BOLD
                navContainerAccount.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
            "Devices" -> {
                fragmentToReplace = devicesFragment
                supportActionBar?.title = "GateKeeper Device History"
                navTextDevices.typeface = Typeface.DEFAULT_BOLD
                navContainerDevices.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
            "Settings" -> {
                fragmentToReplace = SettingsFragment(this)
                supportActionBar?.title = "GateKeeper Settings"
                navButtonSettings.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                navButtonSettings.setImageResource(R.drawable.settings_white)
            }
            "About" -> {
                fragmentToReplace = AboutFragment(this)
                supportActionBar?.title = "About GateKeeper"
                navButtonAbout.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                navButtonAbout.setImageResource(R.drawable.copyright_white)
            }
            "Logout" -> {
                fragmentToReplace = null
            }

        }

        if (fragmentToReplace != null) {
            displayFragment(fragmentToReplace)
        } else { logout() }

        drawer.closeDrawer(GravityCompat.START)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean { return true }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    //SYNC TOGGLE STATE
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
