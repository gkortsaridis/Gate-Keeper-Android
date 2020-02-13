package gr.gkortsaridis.gatekeeper.UI.Account


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.florent37.shapeofview.shapes.RoundRectView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.Utils.GlideApp
import gr.gkortsaridis.gatekeeper.Utils.dp
import io.noties.tumbleweed.Timeline
import io.noties.tumbleweed.Tween
import io.noties.tumbleweed.android.ViewTweenManager
import io.noties.tumbleweed.android.types.Alpha
import io.noties.tumbleweed.android.types.Translation
import io.noties.tumbleweed.equations.Cubic
import kotlinx.android.synthetic.main.fragment_my_account.*
import java.io.ByteArrayOutputStream
import java.io.InputStream


class MyAccountFragment : Fragment() {

    private val user = GateKeeperApplication.user
    private lateinit var sendConfirmation : TextView
    private lateinit var emailConfirmed : LinearLayout
    private lateinit var profileImage : ImageView
    private lateinit var accountImageContainer: LinearLayout
    private lateinit var accountInfoContainer: RoundRectView
    private lateinit var updatePictureBtn: Button
    private lateinit var imageLoading: ProgressBar
    private lateinit var adsContainer: RoundRectView
    private lateinit var adView: AdView

    private lateinit var viewDialog: ViewDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_account, container, false)

        viewDialog = ViewDialog(activity!!)

        emailConfirmed = view.findViewById(R.id.verified_email_link)
        sendConfirmation = view.findViewById(R.id.verify_email_here)
        profileImage = view.findViewById(R.id.profileImage)
        accountImageContainer = view.findViewById(R.id.account_image_container)
        accountInfoContainer = view.findViewById(R.id.account_info_container)
        updatePictureBtn = view.findViewById(R.id.update_profile_image)
        imageLoading = view.findViewById(R.id.image_loading)
        adsContainer = view.findViewById(R.id.adview_container)
        adView = view.findViewById(R.id.adview)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        sendConfirmation.setOnClickListener { sendEmailConfirmation() }
        updatePictureBtn.setOnClickListener { pickImage() }
        emailConfirmed.visibility = if (user?.isEmailVerified!!) View.GONE else View.VISIBLE
        displayUserImg()
        animateContainersIn()
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
        ImagePicker.with(this)
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start { resultCode, data ->
                if (resultCode == Activity.RESULT_OK) {
                    val fileUri = data?.data
                    val inputStream = fileUri?.let { activity!!.contentResolver.openInputStream(it) }
                    val imgData = inputStream?.let { createByteArrayToUpload(it) }
                    if (imgData != null) { uploadImageToFirebase(imgData) }
                }
            }
    }

    private fun uploadImageToFirebase(bytes: ByteArray) {
        imageLoading.visibility = View.VISIBLE
        getUserImageReference().putBytes(bytes).addOnCompleteListener {
            run {
                imageLoading.visibility = View.GONE
                if (it.isSuccessful) {
                    displayUserImg()
                } else {
                    Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getUserImageReference(): StorageReference {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("userImages")
        val a = imagesRef.child(AuthRepository.getUserID()+".jpg")
        return a
    }

    private fun createByteArrayToUpload(inputStream: InputStream): ByteArray {
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        return baos.toByteArray()
    }

    private fun displayUserImg() {
        imageLoading.visibility = View.VISIBLE
        GlideApp
            .with(activity!!)
            .load(getUserImageReference())
            .placeholder(R.drawable.camera)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .listener(object: RequestListener<Drawable>{
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    activity!!.runOnUiThread {
                        imageLoading.visibility = View.GONE
                        profileImage.setPadding(150.dp,150.dp,150.dp,150.dp)
                    }
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    activity!!.runOnUiThread {
                        imageLoading.visibility = View.GONE
                        profileImage.setPadding(0,0,0,0)
                        profileImage.setImageDrawable(resource)
                    }
                    return false
                }
            }).submit()


    }

    private fun animateContainersIn() {
        Timeline.createParallel()
            .push(
                Timeline.createParallel()
                    .push(Tween.to(accountImageContainer, Alpha.VIEW, 1.0f).target(1.0f))
                    .push(Tween.to(accountImageContainer, Translation.XY).target(-370.dp.toFloat(), 0f).ease(Cubic.INOUT).duration(1.0f))
            )
            .push(
                Timeline.createParallel()
                    .pushPause(0.3f)
                    .push(Tween.to(accountInfoContainer, Alpha.VIEW, 1.0f).target(1.0f))
                    .push(Tween.to(accountInfoContainer, Translation.XY).target(370.dp.toFloat(), 0f).ease(Cubic.INOUT).duration(1.0f))
            )
            .push(
                Timeline.createParallel()
                    .pushPause(0.5f)
                    .push(Tween.to(adsContainer, Alpha.VIEW, 1.0f).target(1.0f))
                    .push(Tween.to(adsContainer, Translation.XY).target(-370.dp.toFloat(), 0f).ease(Cubic.INOUT).duration(1.0f))
            )
            .start(ViewTweenManager.get(accountImageContainer))

    }
}
