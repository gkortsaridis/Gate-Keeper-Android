package gr.gkortsaridis.gatekeeper.UI.Devices

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.DeviceModifyListener
import gr.gkortsaridis.gatekeeper.Interfaces.DevicesRetrieveListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Repositories.DeviceRepository
import kotlinx.android.synthetic.main.fragment_device_info.*

class DeviceInfoFragment : BottomSheetDialogFragment(), DeviceModifyListener {

    var device : Device? = null
    var activity: Activity? = null
    private var viewDialog: ViewDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_device_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateDeviceUI()
    }

    fun updateDeviceUI(){
        if (this.device != null) {
            device_os.text = device!!.OS + " " + device!!.versionNum
            if (device!!.OS.toLowerCase() == "android"){ device_os_img.setImageResource(R.drawable.android) }

            device_vendor_text.text = device!!.vendor
            device_nickname.text = device!!.vendor
            if (device!!.nickname == "") {
                device_vendor_container.visibility = View.GONE
                device_lazy_padding.visibility = View.VISIBLE
            }else{
                device_nickname.text = device!!.nickname
                device_vendor_container.visibility = View.VISIBLE
                device_lazy_padding.visibility = View.GONE
            }
            device_last_entrance.text = "Last entrance: ${device!!.formattedDate()}"
        }

        delete_device_button.setOnClickListener { deleteDevice() }
        rename_device_button.setOnClickListener { renameDevice() }
    }

    private fun renameDevice() {
        viewDialog = ViewDialog(activity!!)
        val builder = AlertDialog.Builder(activity)
        val parent = RelativeLayout(activity)
        parent.layoutParams = ViewGroup.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        parent.setPadding(50,50,50,50)
        val input = EditText(activity)
        input.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        input.inputType = InputType.TYPE_CLASS_TEXT
        parent.addView(input)

        builder.setTitle("Rename Device")
        builder.setMessage("Set a nickname for your device")
        builder.setView(parent)

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.setPositiveButton("OK") { _, _ ->
            viewDialog!!.showDialog()
            DeviceRepository.renameDevice(device!!, input.text.toString(), this)
        }

        builder.show()
    }

    private fun deleteDevice() {
        viewDialog = ViewDialog(activity!!)
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Delete Device")
        builder.setMessage("Are you sure you wish to delete this device?")

        builder.setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        builder.setPositiveButton("Yes") { _, _ ->
            viewDialog!!.showDialog()
            DeviceRepository.deleteDevice(device!!,this)
        }

        builder.show()
    }

    private fun reloadDevices() {
        DeviceRepository.retrieveDevicesByAccountID(AuthRepository.getUserID(), object : DevicesRetrieveListener{
            override fun onDevicesRetrieved(devices: ArrayList<Device>) {
                GateKeeperApplication.devices = devices
                viewDialog?.hideDialog()
                (requireParentFragment() as DevicesFragment).updateDeviceList()
            }

            override fun onDeviceRetrieveError(exception: Exception) { }
        })
    }

    override fun onDeviceDeleted() {
        reloadDevices()
        dismiss()
    }

    override fun onDeviceRenamed() {
        reloadDevices()
        updateDeviceUI()
    }

}
