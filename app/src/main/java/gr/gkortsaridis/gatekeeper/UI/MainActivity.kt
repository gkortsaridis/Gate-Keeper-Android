package gr.gkortsaridis.gatekeeper.UI

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.florent37.shapeofview.shapes.RoundRectView
import com.google.android.material.navigation.NavigationView
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.About.AboutFragment
import gr.gkortsaridis.gatekeeper.UI.Account.MyAccountFragment
import gr.gkortsaridis.gatekeeper.UI.Authentication.AuthenticationBaseActivity
import gr.gkortsaridis.gatekeeper.UI.Cards.CardsFragment
import gr.gkortsaridis.gatekeeper.UI.Logins.LoginsFragment
import gr.gkortsaridis.gatekeeper.UI.Notes.NotesFragment
import gr.gkortsaridis.gatekeeper.UI.PasswordGenerator.PasswordGeneratorFragment
import gr.gkortsaridis.gatekeeper.UI.Settings.SettingsFragment
import gr.gkortsaridis.gatekeeper.Utils.GlideApp
import io.noties.tumbleweed.Timeline
import io.noties.tumbleweed.Tween
import io.noties.tumbleweed.android.ViewTweenManager
import io.noties.tumbleweed.android.types.Alpha
import io.noties.tumbleweed.android.types.Scale


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var contentFrame: FrameLayout

    private lateinit var navName: TextView
    private lateinit var navContainerPasswords: RelativeLayout
    private lateinit var navContainerCards: RelativeLayout
    private lateinit var navContainerNotes: RelativeLayout
    private lateinit var navContainerAccount: RelativeLayout
    private lateinit var navContainerPassGen: RelativeLayout
    private lateinit var navButtonSettings: ImageView
    private lateinit var navButtonAbout: ImageView
    private lateinit var navButtonLogout: ImageView

    private lateinit var navTextPasswords: TextView
    private lateinit var navTextCards: TextView
    private lateinit var navTextNotes: TextView
    private lateinit var navTextAccount: TextView
    private lateinit var navTextPassGen: TextView

    private lateinit var profileImage: ImageView
    private lateinit var passwordsRoundRect: RoundRectView
    private lateinit var cardsRoundRect: RoundRectView
    private lateinit var notesRoundRect: RoundRectView
    private lateinit var accountRoundRect: RoundRectView
    private lateinit var passGenRoundRect: RoundRectView
    private lateinit var youAreSecuredTV: TextView

    private var currentFragment = ""
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

                    val bigScale = 1.15f
                    val waitTime = 0.1f
                    val returnAnimationLength = 0.25f
                    val transition = io.noties.tumbleweed.equations.Back.IN

                    youAreSecuredTV.alpha = 0f


                    Timeline.createSequence()
                        .push(
                            Timeline.createParallel()
                                .push(
                                    Timeline.createSequence()
                                        .push(Tween.to(navTextPasswords, Scale.XY, returnAnimationLength).target(bigScale, bigScale).ease(transition))
                                        .push(Tween.to(navTextPasswords, Scale.XY, returnAnimationLength).target(1.0f, 1.0f).ease(transition))
                                )
                                .push(
                                    Timeline.createSequence()
                                        .pushPause(waitTime)
                                        .push(Tween.to(navTextCards, Scale.XY, returnAnimationLength).target(bigScale, bigScale).ease(transition))
                                        .push(Tween.to(navTextCards, Scale.XY, returnAnimationLength).target(1.0f, 1.0f).ease(transition))
                                )
                                .push(
                                    Timeline.createSequence()
                                        .pushPause(waitTime*2)
                                        .push(Tween.to(navTextNotes, Scale.XY, returnAnimationLength).target(bigScale, bigScale).ease(transition))
                                        .push(Tween.to(navTextNotes, Scale.XY, returnAnimationLength).target(1.0f, 1.0f).ease(transition))
                                )
                                .push(
                                    Timeline.createSequence()
                                        .pushPause(waitTime*3)
                                        .push(Tween.to(navTextAccount, Scale.XY, returnAnimationLength).target(bigScale, bigScale).ease(transition))
                                        .push(Tween.to(navTextAccount, Scale.XY, returnAnimationLength).target(1.0f, 1.0f).ease(transition))
                                )
                                .push(
                                    Timeline.createSequence()
                                        .pushPause(waitTime*4)
                                        .push(Tween.to(navTextPassGen, Scale.XY, returnAnimationLength).target(bigScale, bigScale).ease(transition))
                                        .push(Tween.to(navTextPassGen, Scale.XY, returnAnimationLength).target(1.0f, 1.0f).ease(transition))
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
        navContainerPassGen = findViewById(R.id.nav_container_pass_gen)
        navButtonSettings = findViewById(R.id.nav_button_settings)
        navButtonAbout = findViewById(R.id.nav_button_about)
        navButtonLogout = findViewById(R.id.nav_button_logout)
        navTextPasswords = findViewById(R.id.nav_text_passwords)
        navTextCards = findViewById(R.id.nav_text_cards)
        navTextNotes = findViewById(R.id.nav_text_notes)
        navTextPassGen = findViewById(R.id.nav_text_pass_gen)
        navTextAccount = findViewById(R.id.nav_text_account)
        profileImage = findViewById(R.id.profile_image)
        passwordsRoundRect = findViewById(R.id.passwords_rectview)
        cardsRoundRect = findViewById(R.id.cards_rectview)
        notesRoundRect = findViewById(R.id.notes_rectview)
        accountRoundRect = findViewById(R.id.account_rectview)
        passGenRoundRect = findViewById(R.id.pass_gen_rectview)
        youAreSecuredTV = findViewById(R.id.your_are_secured_tv)

        navContainerPasswords.setOnClickListener { switchFragment("Passwords") }
        navContainerCards.setOnClickListener { switchFragment("Cards") }
        navContainerNotes.setOnClickListener { switchFragment("Notes") }
        navContainerPassGen.setOnClickListener { switchFragment("PasswordGenerator") }
        navContainerAccount.setOnClickListener { switchFragment("Account") }
        navButtonSettings.setOnClickListener { switchFragment("Settings") }
        navButtonAbout.setOnClickListener { switchFragment("About") }
        navButtonLogout.setOnClickListener { switchFragment("Logout") }

        GlideApp
            .with(this)
            .load(GateKeeperApplication.extraData?.getUserImgBmp())
            .placeholder(R.mipmap.ic_launcher_round)
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    profileImage.setBackgroundResource(R.mipmap.ic_launcher_round)
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }
            })
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(profileImage)

        val userName = GateKeeperApplication.extraData?.userFullName ?: GateKeeperApplication.extraData?.userEmail
        navName.text = userName
        switchFragment("Passwords")
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
        currentFragment = what
        navTextPasswords.typeface = Typeface.DEFAULT
        navTextNotes.typeface = Typeface.DEFAULT
        navTextCards.typeface = Typeface.DEFAULT
        navTextAccount.typeface = Typeface.DEFAULT
        navTextPassGen.typeface = Typeface.DEFAULT
        navContainerPasswords.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        navContainerCards.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        navContainerNotes.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        navContainerPassGen.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        navContainerAccount.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        navButtonSettings.setBackgroundColor(resources.getColor(android.R.color.transparent))
        navButtonAbout.setBackgroundColor(resources.getColor(android.R.color.transparent))
        navButtonLogout.setBackgroundColor(resources.getColor(android.R.color.transparent))
        navButtonSettings.setImageResource(R.drawable.settings)
        navButtonAbout.setImageResource(R.drawable.copyright)

        when (what) {
            "Passwords" -> {
                fragmentToReplace = LoginsFragment()
                supportActionBar?.title = "Passwords"
                navTextPasswords.typeface = Typeface.DEFAULT_BOLD
                navContainerPasswords.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                //loginsFragment.animateFabIn()
            }
            "Cards" -> {
                fragmentToReplace = CardsFragment()
                supportActionBar?.title = "Digital Wallet"
                navTextCards.typeface = Typeface.DEFAULT_BOLD
                navContainerCards.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
            "Notes" -> {
                fragmentToReplace = NotesFragment()
                supportActionBar?.title = "Secure Notes"
                navTextNotes.typeface = Typeface.DEFAULT_BOLD
                navContainerNotes.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
            "Account" -> {
                fragmentToReplace = MyAccountFragment()
                supportActionBar?.title = "My Account"
                navTextAccount.typeface = Typeface.DEFAULT_BOLD
                navContainerAccount.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
            "PasswordGenerator" -> {
                fragmentToReplace = PasswordGeneratorFragment()
                supportActionBar?.title = "Password Generator"
                navTextPassGen.typeface = Typeface.DEFAULT_BOLD
                navContainerPassGen.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
            "Settings" -> {
                fragmentToReplace = SettingsFragment()
                supportActionBar?.title = "Settings"
                navButtonSettings.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                navButtonSettings.setImageResource(R.drawable.settings_white)
            }
            "About" -> {
                fragmentToReplace = AboutFragment()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fragment", currentFragment)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val lastActiveFragment = (savedInstanceState["fragment"] ?: "") as String
        if (lastActiveFragment != "") {
            switchFragment(lastActiveFragment)
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
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
}
