package gr.gkortsaridis.gatekeeper.UI.Devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.R
import kotlinx.android.synthetic.main.fragment_device_info.*

class DeviceInfoFragment : BottomSheetDialogFragment() {

    private var device : Device? = null
    fun setDevice(device: Device) { this.device = device}

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
            device_last_entrance.text = device!!.formattedDate()
        }
    }

}
