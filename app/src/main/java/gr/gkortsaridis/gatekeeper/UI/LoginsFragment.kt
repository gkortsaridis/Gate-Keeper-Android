package gr.gkortsaridis.gatekeeper.UI


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.LoginRetrieveListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository.retrieveLoginsByAccountID
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.LoginsRecyclerViewAdapter
import java.lang.Exception

class LoginsFragment : Fragment(), LoginRetrieveListener {

    private var TAG = "_LOGINS_FRAGMENT_"

    private lateinit var loginsRV: RecyclerView
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_logins, container, false)
        loginsRV = view.findViewById(R.id.logins_recycler_view)

        retrieveLoginsByAccountID(GateKeeperApplication.userAccount.id!!, this)

        val logins = ArrayList<Login>()
        loginsRV.layoutManager = LinearLayoutManager(activity)
        loginsRV.adapter =
            LoginsRecyclerViewAdapter(
                activity!!.baseContext,
                logins
            )

        return view
    }

    override fun onLoginsRetrieveSuccess(logins: ArrayList<Login>) {
        for (login in logins) {
            Log.i(TAG, login.toString())
        }
    }

    override fun onLoginsRetrieveError(e: Exception) {
        e.printStackTrace()
    }

}
