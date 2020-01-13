package gr.gkortsaridis.gatekeeper.UI.Account


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import kotlinx.android.synthetic.main.fragment_my_account.*
import java.io.ByteArrayOutputStream


class MyAccountFragment(private val activity: Activity) : Fragment() {

    private val user = GateKeeperApplication.user
    private lateinit var sendConfirmation : TextView
    private lateinit var emailConfirmed : LinearLayout
    private lateinit var profileImage : ImageView

    private val PICK_PHOTO_FOR_AVATAR = 1
    private val viewDialog = ViewDialog(activity)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_account, container, false)

        emailConfirmed = view.findViewById(R.id.verified_email_link)
        sendConfirmation = view.findViewById(R.id.verify_email_here)
        profileImage = view.findViewById(R.id.profileImage)

        sendConfirmation.setOnClickListener { sendEmailConfirmation() }
        profileImage.setOnClickListener { pickImage() }
        emailConfirmed.visibility = if (user?.isEmailVerified!!) View.GONE else View.VISIBLE
        displayUserImg(user)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        full_name_et.setText(user?.displayName ?: "")
        email_et.setText(user?.email ?: "")
        verified_email_link.visibility = if (user?.isEmailVerified == true) { View.GONE } else { View.VISIBLE }
        user_uid.text = "Your personal ID is: ${user?.uid}"

        update_account_btn.setOnClickListener { updateProfile(name = full_name_et.text.toString(), imgUri = null) }
    }

    private fun sendEmailConfirmation(){
        user?.sendEmailVerification()
        Toast.makeText(activity, "Verification email sent", Toast.LENGTH_SHORT).show()
    }

    private fun updateProfile(name: String?, imgUri: Uri?) {
        viewDialog.showDialog()

        //Display Name & Photo URL
        val profileUpdates = UserProfileChangeRequest.Builder()
        if (name != null) { profileUpdates.setDisplayName(full_name_et.text.toString()) }
        if (imgUri != null) { profileUpdates.setPhotoUri(imgUri) }
        val profile = profileUpdates.build()

        user?.updateProfile(profile)?.addOnCompleteListener {
            viewDialog.hideDialog()
            displayUserImg(user)
            Toast.makeText(activity, "Profile Update Completed successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val inputStream = data.data?.let { activity.contentResolver.openInputStream(it) }
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                val storageRef = FirebaseStorage.getInstance().reference
                val imagesRef = storageRef.child("userImages")
                val userImageRef = imagesRef.child(AuthRepository.getUserID()+".jpg")

                userImageRef.putBytes(data).addOnCompleteListener {
                    run {
                        if (it.isSuccessful) {
                            updateProfile(name = null, imgUri = it.result?.uploadSessionUri?.normalizeScheme())
                            Toast.makeText(activity, "DONE", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

    private fun displayUserImg(user: FirebaseUser) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("userImages")
        val userImageRef = imagesRef.child(AuthRepository.getUserID()+".jpg")

        //TODO: Load image into ImageView
        //Glide.with(activity).load(userImageRef).into(profileImage)
    }
}
