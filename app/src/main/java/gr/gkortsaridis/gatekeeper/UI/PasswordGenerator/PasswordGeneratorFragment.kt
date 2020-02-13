package gr.gkortsaridis.gatekeeper.UI.PasswordGenerator


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import gr.gkortsaridis.gatekeeper.R

class PasswordGeneratorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_generator, container, false)
    }


}
