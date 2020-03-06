package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.R
import net.cachapa.expandablelayout.ExpandableLayout

class DeviceHistoryRecyclerViewAdapter(
    private val context: Context,
    private var devices: ArrayList<Device>): RecyclerView.Adapter<DeviceHistoryRecyclerViewAdapter.DeviceHistoryViewHolder>() {

    private val HEADER = 0
    private val DATA = 1
    private val FOOTER = 2

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return HEADER
        else if (position == devices.size - 1) return FOOTER
        return DATA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHistoryViewHolder {
        val inflatedView =
            when(viewType) {
                HEADER -> LayoutInflater.from(context).inflate(R.layout.recycler_view_item_history_header, parent, false)
                DATA -> LayoutInflater.from(context).inflate(R.layout.recycler_view_item_history_device_data, parent, false)
                FOOTER -> LayoutInflater.from(context).inflate(R.layout.recycler_view_item_history_footer, parent, false)
                else -> LayoutInflater.from(context).inflate(R.layout.recycler_view_item_history_data, parent, false)
            }

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

        /*private var logAction: TextView? = null
        private var logTimestamp: TextView? = null
        private var logDevice: TextView? = null*/
        private var expandBtn: Button? = null
        private var deviceInfoView: ExpandableLayout? = null
        private var view: View = v

        init {
            //logAction = view.findViewById(R.id.history_action)
            //logTimestamp = view.findViewById(R.id.history_timestamp)
            //logDevice = view.findViewById(R.id.history_device)
            expandBtn = view.findViewById(R.id.expand_device_info)
            deviceInfoView = view.findViewById(R.id.device_info_expand)
        }

        fun bindLogin(device: Device, position: Int, context: Context){

            expandBtn?.setOnClickListener { deviceInfoView?.toggle() }

            /*logAction?.text = log.action

            logTimestamp?.text = DateFormat.getDateInstance(DateFormat.FULL).format(log.timestamp)

            val device = DeviceRepository.getDeviceById(log.deviceId.toString())
            val deviceName = when {
                device?.nickname?.isNotBlank() == true -> {
                    device.nickname
                }
                device?.vendor?.isNotBlank() == true -> {
                    device.vendor + " (" + device.OS + " " + device.version + ")"
                }
                else -> {
                    "Unknown Device"
                }
            }
            logDevice?.text = "From: $deviceName"*/
        }

    }
}