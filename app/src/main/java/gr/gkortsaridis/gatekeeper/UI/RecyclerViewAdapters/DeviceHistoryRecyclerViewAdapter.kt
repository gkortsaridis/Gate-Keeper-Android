package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.R
import io.noties.tumbleweed.Tween
import io.noties.tumbleweed.android.ViewTweenManager
import io.noties.tumbleweed.android.types.Rotation
import net.cachapa.expandablelayout.ExpandableLayout

class DeviceHistoryRecyclerViewAdapter(
    private val context: Context,
    private var devices: ArrayList<Device>): RecyclerView.Adapter<DeviceHistoryRecyclerViewAdapter.DeviceHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHistoryViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_history_device_data, parent, false)
        return DeviceHistoryViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: DeviceHistoryViewHolder, position: Int) {
        val deviceItem = devices[position]
        holder.bindLogin(deviceItem, position, context)
    }

    fun updateDevices(devices: ArrayList<Device>) {
        this.devices = devices
        notifyDataSetChanged()
    }

    class DeviceHistoryViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private var deviceNickname: TextView? = null
        private var deviceVendor: TextView? = null
        private var deviceLastUsed: TextView? = null
        private var deviceOS: TextView? = null
        private var expandBtn: ImageButton? = null
        private var deviceInfoView: ExpandableLayout? = null
        private var view: View = v

        init {
            deviceNickname = view.findViewById(R.id.device_nickname)
            deviceVendor = view.findViewById(R.id.device_vendor)
            deviceLastUsed = view.findViewById(R.id.device_last_used)
            expandBtn = view.findViewById(R.id.expand_device_info)
            deviceInfoView = view.findViewById(R.id.device_info_expand)
            deviceOS = view.findViewById(R.id.device_os_info)
        }

        fun bindLogin(device: Device, position: Int, context: Context){
            expandBtn?.setOnClickListener {
                deviceInfoView?.toggle()
                val target = if (deviceInfoView?.isExpanded == true) 180.0f else 0.0f
                Tween.to(expandBtn!!, Rotation.I, 1.0f).target(target).start(ViewTweenManager.get(expandBtn!!))
            }
            deviceNickname?.text = if (device.nickname.isNotBlank()) device.nickname else device.vendor
            deviceVendor?.text = device.vendor
            deviceVendor?.visibility = if (device.nickname.isNotBlank()) View.VISIBLE else View.GONE
            deviceOS?.text = device.OS + " " +device.version + " ("+device.versionNum+")"
        }

    }
}