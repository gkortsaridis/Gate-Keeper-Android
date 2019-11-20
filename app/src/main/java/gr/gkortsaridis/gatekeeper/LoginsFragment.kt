package gr.gkortsaridis.gatekeeper


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class LoginsFragment : Fragment() {

    private var TAG = "_LOGINS_FRAGMENT_"

    private lateinit var loginsRV: RecyclerView
    private lateinit var db: FirebaseFirestore

    override fun onStart() {
        super.onStart()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_logins, container, false)
        loginsRV = view.findViewById(R.id.logins_recycler_view)

        retrieveLoginsByAccountID()

        val logins = ArrayList<Login>()
        loginsRV.layoutManager = LinearLayoutManager(activity)
        loginsRV.adapter = LoginsRecyclerViewAdapter(activity!!.baseContext, logins)

        return view
    }

    private fun retrieveLoginsByAccountID() {
        val accountID = GateKeeperApplication.userAccount.id

        Log.i(TAG, "Retrieving Logins for Account : $accountID")

        db = FirebaseFirestore.getInstance()
        db.collection("logins")
            .whereEqualTo("account_id",accountID)
            .get().addOnSuccessListener { result ->
            for (document in result) {
                val login = Login(document)
                Log.i(TAG, login.toString())
            }
        }
    }

}
