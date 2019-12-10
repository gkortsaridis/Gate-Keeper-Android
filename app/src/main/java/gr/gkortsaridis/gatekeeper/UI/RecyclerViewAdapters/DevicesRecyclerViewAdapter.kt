package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.Interfaces.CreditCardClickListener
import gr.gkortsaridis.gatekeeper.R

class DevicesRecyclerViewAdapter(
    private val context: Context,
    private val devices: ArrayList<Device>,
    private val listener: CreditCardClickListener?
): RecyclerView.Adapter<DevicesRecyclerViewAdapter.DeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_device, parent, false)
        return DeviceViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val vaultItem = devices[position]
        holder.bindDevice(vaultItem, listener)
    }

    class DeviceViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private var deviceIcon: ImageView? = null
        private var deviceName: TextView? = null
        private var entryDate: TextView? = null
        private var view: View = v

        init {
            deviceIcon = view.findViewById(R.id.deviceIcon)
            deviceName = view.findViewById(R.id.deviceName)
            entryDate = view.findViewById(R.id.entryDate)
        }

        fun bindDevice(device: Device, listener: CreditCardClickListener?){
            this.deviceName?.text = device.vendor
            this.entryDate?.text = device.lastEntry.toString()

            //view.setOnClickListener{ listener.onVaultClicked(vault) }
            //this.vaultName?.text = vault.name
        }

    }
}