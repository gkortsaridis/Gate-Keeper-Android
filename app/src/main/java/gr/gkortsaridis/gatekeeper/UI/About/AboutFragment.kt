package gr.gkortsaridis.gatekeeper.UI.About


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gr.gkortsaridis.gatekeeper.BuildConfig
import gr.gkortsaridis.gatekeeper.R
import kotlinx.android.synthetic.main.fragment_about.*


class AboutFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gatekeeper_version.text = "GateKeeper v"+BuildConfig.VERSION_NAME
    }

}
