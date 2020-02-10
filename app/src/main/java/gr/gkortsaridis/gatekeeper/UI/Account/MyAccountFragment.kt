package gr.gkortsaridis.gatekeeper.UI.Account


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
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
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Utils.GlideApp
import gr.gkortsaridis.gatekeeper.Utils.dp
import kotlinx.android.synthetic.main.fragment_my_account.*
import java.io.ByteArrayOutputStream
import java.io.InputStream


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
        displayUserImg()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        full_name_et.setText(user?.displayName ?: "")
        email_et.setText(user?.email ?: "")
        verified_email_link.visibility = if (user?.isEmailVerified == true) { View.GONE } else { View.VISIBLE }
        user_uid.text = "Your personal ID is: ${user?.uid}"

        update_account_btn.setOnClickListener { updateProfile(name = full_name_et.text.toString()) }
    }

    private fun sendEmailConfirmation(){
        user?.sendEmailVerification()
        Toast.makeText(activity, "Verification email sent", Toast.LENGTH_SHORT).show()
    }

    private fun updateProfile(name: String?) {
        viewDialog.showDialog()

        //Display Name & Photo URL
        val profileUpdates = UserProfileChangeRequest.Builder()
        if (name != null) { profileUpdates.setDisplayName(full_name_et.text.toString()) }
        val profile = profileUpdates.build()

        user?.updateProfile(profile)?.addOnCompleteListener {
            viewDialog.hideDialog()
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
                //Retrieve selected Image
                val inputStream = data.data?.let { activity.contentResolver.openInputStream(it) }
                val imgData = inputStream?.let { createByteArrayToUpload(it) }
                if (imgData != null) {
                    getUserImageReference().putBytes(imgData).addOnCompleteListener {
                        run {
                            if (it.isSuccessful) {
                                displayUserImg()
                            } else {
                                Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

    }

    private fun getUserImageReference(): StorageReference {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("userImages")
        return imagesRef.child(AuthRepository.getUserID()+".jpg")
    }

    private fun createByteArrayToUpload(inputStream: InputStream): ByteArray {
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        return baos.toByteArray()
    }

    private fun displayUserImg() {
        GlideApp
            .with(activity)
            .load(getUserImageReference())
            .placeholder(R.drawable.camera)
            .listener(object: RequestListener<Drawable>{
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    profileImage.setPadding(150.dp,150.dp,150.dp,150.dp)
                    profileImage.setImageResource(R.drawable.camera)
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    profileImage.setPadding(0,0,0,0)
                    return false
                }
            })
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(profileImage)

    }
}
