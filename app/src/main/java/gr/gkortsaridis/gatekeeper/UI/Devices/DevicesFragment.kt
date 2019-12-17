package gr.gkortsaridis.gatekeeper.UI.Devices


import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.DeviceClickListener

import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.DevicesRecyclerViewAdapter


class DevicesFragment(private val activity: Activity) : Fragment(), DeviceClickListener {

    private lateinit var devicesRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_devices, container, false)

        devicesRecyclerView = view.findViewById(R.id.devicesRV)
        devicesRecyclerView.layoutManager = LinearLayoutManager(context!!)
        updateDeviceList()

        return view
    }

    override fun onDeviceClicked(device: Device) {
        val deviceInfoFragment = DeviceInfoFragment()
        deviceInfoFragment.device = device
        deviceInfoFragment.activity = activity
        val transaction = childFragmentManager.beginTransaction()
        deviceInfoFragment.show(transaction, "TAG")
    }

    fun updateDeviceList() {
        devicesRecyclerView.adapter = DevicesRecyclerViewAdapter(context!!, GateKeeperApplication.devices!!, this)
    }


}
