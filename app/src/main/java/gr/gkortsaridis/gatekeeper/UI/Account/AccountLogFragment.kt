package gr.gkortsaridis.gatekeeper.UI.Account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import gr.gkortsaridis.gatekeeper.Entities.CompactUserLog
import gr.gkortsaridis.gatekeeper.Entities.UserLog
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.LogHistoryRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_account_devices.*
import kotlinx.android.synthetic.main.fragment_account_log.*

class AccountLogFragment : Fragment() {

    private lateinit var viewDialog: ViewDialog
    private lateinit var logAdapter: LogHistoryRecyclerViewAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDialog = ViewDialog(activity!!)

        logAdapter = LogHistoryRecyclerViewAdapter(context!!, ArrayList())
        log_recycler_view.layoutManager = LinearLayoutManager(context)
        log_recycler_view.addItemDecoration(DividerItemDecoration(context, 0))
        log_recycler_view.adapter = logAdapter

        swipe_refresh.setOnRefreshListener {
            val act = activity as AccountHistoryActivity
            act.retrieveLogs()
        }
    }

    fun updateLogs() {
        val logs = GateKeeperApplication.userLog
        logs.sortBy { it.timestamp }
        val sortedAndReversed = ArrayList(logs.reversed())

        val compactUserLogs = ArrayList<CompactUserLog>()
        var addToCompactList = false
        sortedAndReversed.forEachIndexed { index, userLog ->
            if (!addToCompactList) {
                val item = ArrayList<UserLog>()
                item.add(userLog)
                val compactUserLog = CompactUserLog(item)
                compactUserLogs.add(compactUserLog)
            } else {
                val topList = compactUserLogs[compactUserLogs.size-1]
                topList.sameLogs.add(userLog)
            }
            if (index < logs.size - 1) {
                addToCompactList = userLog.action == logs[index+1].action
            } else { addToCompactList = false }
        }
        logAdapter.updateLogHistory(sortedAndReversed)
    }

}
