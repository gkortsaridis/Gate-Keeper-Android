package gr.gkortsaridis.gatekeeper.UI.Account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.DeviceModifyListener

import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.DeviceHistoryRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_account_devices.*
import kotlinx.android.synthetic.main.fragment_account_log.*

class AccountDevicesFragment : Fragment(), DeviceModifyListener {

    private lateinit var viewDialog: ViewDialog
    private lateinit var adapter: DeviceHistoryRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_devices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDialog = ViewDialog(activity!!)
        val devices = GateKeeperApplication.devices ?: ArrayList()
        adapter = DeviceHistoryRecyclerViewAdapter(context!!, devices, this)
        devices_recycler_view.layoutManager = LinearLayoutManager(context)
        devices_recycler_view.addItemDecoration(DividerItemDecoration(context, 0))
        devices_recycler_view.adapter = adapter
    }

    override fun onDeviceDeleteRequest(device: Device) {

    }

    override fun onDeviceRenameRequest(device: Device) {
        viewDialog.showDialog()
        val disposable = GateKeeperAPI.api.updateDevice(SecurityRepository.createEncryptedDataRequestBody(device, device.id))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    viewDialog.hideDialog()
                    val decryptedDevice = SecurityRepository.decryptEncryptedDataToObjectWithUserCredentials(it.data, Device::class.java) as Device?
                    if (decryptedDevice != null) {
                        decryptedDevice.id = it.data.id.toString()
                        if (it.errorCode == -1) {
                            Log.i("SUCCESS", decryptedDevice.toString())
                            GateKeeperApplication.devices?.replaceAll { dev -> if (dev.id == decryptedDevice.id) decryptedDevice else dev }
                            Toast.makeText(activity, "Device successfully renamed", Toast.LENGTH_SHORT).show()
                            adapter.updateDevices(GateKeeperApplication.devices ?: ArrayList())
                        }
                        else {
                            Log.i("SUCCESS", decryptedDevice.toString())
                        }
                    } else {
                        Log.i("ERROR","ERROR")
                    }
                },
                {
                    val i = it
                    Log.i("ERROR", "ERROR")
                }
            )
    }


}
