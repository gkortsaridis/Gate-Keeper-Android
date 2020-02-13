package gr.gkortsaridis.gatekeeper.UI.Devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.shapeofview.shapes.RoundRectView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import gr.gkortsaridis.gatekeeper.Entities.Device
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.DeviceClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.DevicesRecyclerViewAdapter
import gr.gkortsaridis.gatekeeper.Utils.dp
import io.noties.tumbleweed.Timeline
import io.noties.tumbleweed.Tween
import io.noties.tumbleweed.android.ViewTweenManager
import io.noties.tumbleweed.android.types.Alpha
import io.noties.tumbleweed.android.types.Translation
import io.noties.tumbleweed.equations.Cubic

class DevicesFragment : Fragment(), DeviceClickListener {

    private lateinit var devicesRecyclerView: RecyclerView
    private lateinit var adView: AdView
    private lateinit var adViewContainer: RoundRectView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_devices, container, false)

        adView = view.findViewById(R.id.adview)
        adViewContainer = view.findViewById(R.id.adview_container)
        devicesRecyclerView = view.findViewById(R.id.devicesRV)
        devicesRecyclerView.layoutManager = LinearLayoutManager(context!!)
        updateDeviceList()

        MobileAds.initialize(context!!, GateKeeperApplication.admobAppID)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        animateAdIn()

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


    private fun animateAdIn() {
        Timeline.createParallel()
            .push(Tween.to(adViewContainer, Alpha.VIEW, 1.0f).target(1.0f))
            .push(Tween.to(adViewContainer, Translation.XY).target(0f,-90.dp.toFloat()).ease(Cubic.INOUT).duration(1.0f))
            .start(ViewTweenManager.get(adViewContainer))
    }

}
