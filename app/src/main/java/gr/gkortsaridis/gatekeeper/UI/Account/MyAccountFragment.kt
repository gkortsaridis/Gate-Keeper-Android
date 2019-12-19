package gr.gkortsaridis.gatekeeper.UI.Account


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.UserProfileChangeRequest
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import kotlinx.android.synthetic.main.fragment_my_account.*

class MyAccountFragment : Fragment() {

    private val user = GateKeeperApplication.user

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val g = user?.photoUrl

        full_name_et.setText(user?.displayName ?: "")
        email_et.setText(user?.email ?: "")
        verified_email_link.visibility = if (user?.isEmailVerified == true) { View.GONE } else { View.VISIBLE }
        user_uid.text = "Your personal ID is: ${user?.uid}"

        update_account_btn.setOnClickListener { updateProfile() }

    }

    private fun updateProfile() {
        val viewDialog = ViewDialog(activity!!)
        viewDialog.showDialog()

        //Display Name & Photo URL
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(full_name_et.text.toString())
            //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
            .build()
        user?.updateProfile(profileUpdates)?.addOnCompleteListener {

            //Email
            if (email_et.text.toString() != user.email) {
                user.updateEmail(email_et.text.toString()).addOnCompleteListener {
                    //TODO: Redownload FirebaseUser with new email

                    viewDialog.hideDialog()

                    if (it.isSuccessful) {
                        Toast.makeText(context, "Email successfully changed", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, it.exception?.localizedMessage ?: "We encountered an error", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                viewDialog.hideDialog()
            }

        }
    }


}
