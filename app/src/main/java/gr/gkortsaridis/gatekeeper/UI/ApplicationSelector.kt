package gr.gkortsaridis.gatekeeper.UI

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Interfaces.ApplicationSelectListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.ApplicationSelectRecyclerViewAdapter

class ApplicationSelector : AppCompatActivity(), ApplicationSelectListener {

    private lateinit var applicationRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_selector)

        applicationRV = findViewById(R.id.application_list)

        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pkgAppsList: List<ResolveInfo> = packageManager.queryIntentActivities(mainIntent, 0)

        applicationRV.adapter = ApplicationSelectRecyclerViewAdapter(this, pkgAppsList, packageManager, this)
        applicationRV.layoutManager = LinearLayoutManager(this)

    }

    override fun onApplicationSelected(app: ResolveInfo) {
        val output = Intent()
        output.putExtra("app", app)
        setResult(Activity.RESULT_OK, output)
        finish()
    }
}
