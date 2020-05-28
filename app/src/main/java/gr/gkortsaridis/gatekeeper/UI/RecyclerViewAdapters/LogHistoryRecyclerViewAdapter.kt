package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.UserLog
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.DeviceRepository
import gr.gkortsaridis.gatekeeper.Utils.LogHistory
import java.text.DateFormat


class LogHistoryRecyclerViewAdapter(
    private val context: Context,
    private var logs: ArrayList<UserLog>): RecyclerView.Adapter<LogHistoryRecyclerViewAdapter.LogHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogHistoryViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_history_data, parent, false)
        return LogHistoryViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    override fun onBindViewHolder(holder: LogHistoryViewHolder, position: Int) {
        val logItem = logs[position]
        holder.bindLogin(logItem, position, context)
    }

    fun updateLogHistory(logs: ArrayList<UserLog>) {
        this.logs = logs
        notifyDataSetChanged()
    }

    class LogHistoryViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private var logAction: TextView? = null
        private var logTimestamp: TextView? = null
        private var logDevice: TextView? = null
        private var view: View = v

        init {
            logAction = view.findViewById(R.id.history_action)
            logTimestamp = view.findViewById(R.id.history_timestamp)
            logDevice = view.findViewById(R.id.history_device)
        }

        fun bindLogin(log: UserLog, position: Int, context: Context){
            logAction?.text = LogHistory.getFormattedLog(log.action)

            logTimestamp?.text = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(log.timestamp)

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
            logDevice?.text = "From: $deviceName"
        }

    }
}