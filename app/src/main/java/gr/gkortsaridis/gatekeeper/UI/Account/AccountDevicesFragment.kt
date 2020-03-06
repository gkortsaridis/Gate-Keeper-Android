package gr.gkortsaridis.gatekeeper.UI.Account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.GateKeeperApplication

import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.DeviceHistoryRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_account_devices.*
import kotlinx.android.synthetic.main.fragment_account_log.*

class AccountDevicesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_devices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val devices = GateKeeperApplication.devices ?: ArrayList()
        devices.add(0, Device())
        devices.add(Device())
        devices_recycler_view.layoutManager = LinearLayoutManager(context)
        devices_recycler_view.addItemDecoration(DividerItemDecoration(context, 0))
        devices_recycler_view.adapter = DeviceHistoryRecyclerViewAdapter(context!!, devices)
    }
}
