package gr.gkortsaridis.gatekeeper.UI.Logins

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Interfaces.ApplicationSelectListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.ApplicationSelectRecyclerViewAdapter
import java.util.*


class ApplicationSelector : AppCompatActivity(), ApplicationSelectListener {

    private lateinit var toolbar: Toolbar
    private lateinit var applicationRV: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var applicationRVadapter: ApplicationSelectRecyclerViewAdapter
    private lateinit var pkgAppsList: List<ResolveInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_selector)

        applicationRV = findViewById(R.id.application_list)
        toolbar = findViewById(R.id.toolbar)
        searchView = findViewById(R.id.search_view)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.title = "Set your login URL"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        pkgAppsList = packageManager.queryIntentActivities(mainIntent, 0)

        Collections.sort(pkgAppsList) { x: ResolveInfo, y: ResolveInfo -> x.loadLabel(packageManager).toString().compareTo(y.loadLabel(packageManager).toString())}

        applicationRVadapter = ApplicationSelectRecyclerViewAdapter(this, ArrayList(pkgAppsList), packageManager, this)
        applicationRV.adapter = applicationRVadapter
        applicationRV.layoutManager = LinearLayoutManager(this)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                updateApplicationsList(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateApplicationsList(newText ?: "")
                return true
            }
        })

    }

    private fun updateApplicationsList(query: String) {
        if (query != "") {
            var tempData = pkgAppsList
            tempData = tempData.filter {
                it.loadLabel(packageManager).toString().toLowerCase().contains(query)
            }
            applicationRVadapter.refreshDataSet(ArrayList(tempData))
        }else{
            applicationRVadapter.refreshDataSet(ArrayList(pkgAppsList))
        }
    }

    override fun onApplicationSelected(app: ResolveInfo) {
        val output = Intent()
        output.putExtra("app", app)
        setResult(Activity.RESULT_OK, output)
        finish()
    }

}
