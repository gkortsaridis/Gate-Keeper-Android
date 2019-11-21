package gr.gkortsaridis.gatekeeper.UI


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.LoginsRecyclerViewAdapter

class LoginsFragment : Fragment() {

    private var TAG = "_LOGINS_FRAGMENT_"

    private lateinit var loginsRV: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var vaultName: TextView
    private lateinit var folderName: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_logins, container, false)

        //RecyclerView Initialization
        loginsRV = view.findViewById(R.id.logins_recycler_view) as RecyclerView
        loginsRV.layoutManager = LinearLayoutManager(activity)
        loginsRV.adapter =
            LoginsRecyclerViewAdapter(
                activity!!.baseContext,
                GateKeeperApplication.logins
            )

        fab = view.findViewById(R.id.fab)
        fab.setOnClickListener{ startActivity(Intent(activity, CreateLoginActivity::class.java))}

        vaultName = view.findViewById(R.id.vault_name)
        folderName = view.findViewById(R.id.folder_name)

        vaultName.text = GateKeeperApplication.activeVault.name
        folderName.text = GateKeeperApplication.activeFolder.name

        return view
    }

}
