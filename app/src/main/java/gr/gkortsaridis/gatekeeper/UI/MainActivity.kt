package gr.gkortsaridis.gatekeeper.UI

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.Cards.CardsFragment
import gr.gkortsaridis.gatekeeper.UI.Logins.LoginsFragment


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = MainActivity::class.java.simpleName

    val eCryptSymmetric = ECSymmetric()

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var contentFrame: FrameLayout

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        displayFragment(CardsFragment(this))
        //displayFragment(LoginsFragment(this))
    }

    private fun displayFragment(fragment: Fragment?){
        if (fragment != null) {
            supportFragmentManager.beginTransaction().replace(contentFrame.id, fragment, "ContentFragment").commit()
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        var fragmentToReplace: Fragment? = null

        when (p0.itemId) {
            R.id.nav_item_logins -> { fragmentToReplace =
                LoginsFragment(this)
            }
            R.id.nav_item_cards -> { fragmentToReplace =
                CardsFragment(this)
            }
            R.id.nav_item_account -> { fragmentToReplace =
                AccountFragment()
            }
            R.id.nav_item_settings -> { fragmentToReplace =
                LoginsFragment(this)
            }
            R.id.nav_item_devices -> { fragmentToReplace =
                LoginsFragment(this)
            }
            R.id.nav_item_about -> { fragmentToReplace =
                LoginsFragment(this)
            }
        }

        displayFragment(fragmentToReplace)

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

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
