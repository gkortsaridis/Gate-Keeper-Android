package gr.gkortsaridis.gatekeeper.UI.Logins


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginSelectListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository.createLoginRequestCode
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository.createLoginSuccess
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.LoginsRecyclerViewAdapter

class LoginsFragment(private var activity: Activity) : Fragment(), LoginSelectListener {

    private val TAG = "_LOGINS_FRAGMENT_"

    private lateinit var loginsRV: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var vaultName: TextView
    private lateinit var folderName: TextView
    private lateinit var vaultView: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_logins, container, false)

        //RecyclerView Initialization
        loginsRV = view.findViewById(R.id.logins_recycler_view) as RecyclerView
        loginsRV.layoutManager = LinearLayoutManager(activity)


        fab = view.findViewById(R.id.fab)
        vaultView = view.findViewById(R.id.vault_view)
        vaultName = view.findViewById(R.id.vault_name)

        fab.setOnClickListener{ startActivityForResult(Intent(activity, CreateLoginActivity::class.java), createLoginRequestCode)}
        vaultView.setOnClickListener{ startActivityForResult(Intent(activity, SelectVaultActivity::class.java), createLoginRequestCode)}

        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        loginsRV.adapter =
            LoginsRecyclerViewAdapter(
                activity.baseContext,
                LoginsRepository.filterLoginsByCurrentVaultAndFolder(GateKeeperApplication.logins),
                activity.packageManager,
                this
            )

        vaultName.text = GateKeeperApplication.activeVault.name
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == createLoginRequestCode && resultCode == createLoginSuccess) {
            updateUI()
            Toast.makeText(context, "Login successfully created", Toast.LENGTH_SHORT).show()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onLoginClicked(login: Login) {
        Log.i("Clicked", login.name)
        val intent = Intent(activity, CreateLoginActivity::class.java)
        intent.putExtra("login_id",login.id)
        startActivity(intent)
    }
}
