package gr.gkortsaridis.gatekeeper.UI.Devices


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.GateKeeperApplication

import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.DevicesRecyclerViewAdapter


class DevicesFragment : Fragment() {

    private lateinit var devicesRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_devices, container, false)

        devicesRecyclerView = view.findViewById(R.id.devicesRV)

        devicesRecyclerView.adapter = DevicesRecyclerViewAdapter(context!!, GateKeeperApplication.devices!!, null)
        devicesRecyclerView.layoutManager = LinearLayoutManager(context!!)

        return view
    }


}
