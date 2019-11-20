package gr.gkortsaridis.gatekeeper


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LoginsFragment : Fragment() {

    private lateinit var loginsRV: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_logins, container, false)

        loginsRV = view.findViewById(R.id.logins_recycler_view)

        val logins = ArrayList<Login>()
        logins.add(Login("Personal Google", "gkortsaridis@gmail.com", "password1", "url1", "note1"))
        logins.add(Login("Twitter", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("AppleID", "gkortsaridis@gmail.com", "password1", "url1", "note1"))
        logins.add(Login("Facebook", "gkortsaridis@gmail.com", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))
        logins.add(Login("Instagram", "gkortsaridis", "password1", "url1", "note1"))

        loginsRV.layoutManager = LinearLayoutManager(activity)
        loginsRV.adapter = LoginsRecyclerViewAdapter(activity!!.baseContext, logins)

        return view
    }


}
