package gr.gkortsaridis.gatekeeper.UI.Account


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.ads.AdRequest
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AnalyticsRepository
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import gr.gkortsaridis.gatekeeper.Utils.GlideApp
import gr.gkortsaridis.gatekeeper.Utils.dp
import io.noties.tumbleweed.Timeline
import io.noties.tumbleweed.Tween
import io.noties.tumbleweed.android.ViewTweenManager
import io.noties.tumbleweed.android.types.Alpha
import io.noties.tumbleweed.android.types.Translation
import io.noties.tumbleweed.equations.Cubic
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_my_account.*
import java.io.ByteArrayOutputStream
import java.io.InputStream

class MyAccountFragment : Fragment() {

    private lateinit var viewDialog: ViewDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDialog = ViewDialog(activity!!)

        full_name_et.setText(GateKeeperApplication.extraData?.userFullName ?: "")
        email_et.setText(GateKeeperApplication.extraData?.userEmail)
        user_uid.text = "Your personal ID is: ${GateKeeperApplication.user_id ?: ""}"
        update_account_btn.setOnClickListener { updateProfileName(name = full_name_et.text.toString()) }

        update_profile_image.setOnClickListener { pickImage() }
        account_billing_container.setOnClickListener { goToBilling() }
        account_history_container.setOnClickListener { goToHistory() }

        val adRequest = AdRequest.Builder().build()
        adview.loadAd(adRequest)

        displayUserImg()
        animateContainersIn()
    }

    private fun goToHistory() {
        AnalyticsRepository.trackEvent(AnalyticsRepository.HISTORY_PAGE)
        startActivity(Intent(activity, AccountHistoryActivity::class.java))
    }

    private fun goToBilling() {
        AnalyticsRepository.trackEvent(AnalyticsRepository.BILLING_PAGE)
        Toast.makeText(context, "Everything is free for now :)", Toast.LENGTH_SHORT).show()
        //context?.startActivity(Intent(activity, AccountStatusActivity::class.java))
    }

    private fun updateProfileName(name: String?) {
        viewDialog.showDialog()

        val disposable = GateKeeperAPI.api.updateExtraData(SecurityRepository.createExtraDataUpdateRequestBody(fullName = name))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    viewDialog.hideDialog()
                    if (it.errorCode == -1) {
                        GateKeeperApplication.extraData =  SecurityRepository.getUserExtraData(
                            GateKeeperApplication.extraData?.userEmail ?: "",
                            it.data.extraDataEncryptedData,
                            it.data.extraDataIv)
                        AnalyticsRepository.trackEvent(AnalyticsRepository.ACCOUNT_NAME_CHANGE)
                        Toast.makeText(activity, "Data successfully updated", Toast.LENGTH_SHORT).show()
                    }
                    else { Toast.makeText(activity, "Could not update your data", Toast.LENGTH_SHORT).show() }
                },
                { Toast.makeText(activity, "Could not update your data", Toast.LENGTH_SHORT).show() }
            )
    }

    private fun updateProfileImg(img: String) {
        viewDialog.showDialog()

        val disposable = GateKeeperAPI.api.updateExtraData(SecurityRepository.createExtraDataUpdateRequestBody(imgUrl = img))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    viewDialog.hideDialog()
                    if (it.errorCode == -1) {
                        GateKeeperApplication.extraData =  SecurityRepository.getUserExtraData(
                            GateKeeperApplication.extraData?.userEmail ?: "",
                            it.data.extraDataEncryptedData,
                            it.data.extraDataIv)

                        displayUserImg()
                        AnalyticsRepository.trackEvent(AnalyticsRepository.ACCOUNT_ICON_CHANGE)
                        Toast.makeText(activity, "Img successfully updated", Toast.LENGTH_SHORT).show()
                    }
                    else { Toast.makeText(activity, "Could not update your data", Toast.LENGTH_SHORT).show() }
                },
                { Toast.makeText(activity, "Could not update your data", Toast.LENGTH_SHORT).show() }
            )
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
                    if (imgData != null) { updateProfileImg( Base64.encodeToString(imgData, Base64.DEFAULT) ) }
                }
            }
    }

    private fun createByteArrayToUpload(inputStream: InputStream): ByteArray {
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        return baos.toByteArray()
    }

    private fun displayUserImg() {
        image_loading.visibility = View.VISIBLE
        GlideApp
            .with(activity!!)
            .load(GateKeeperApplication.extraData?.getUserImgBmp())
            .placeholder(R.drawable.camera)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .listener(object: RequestListener<Drawable>{
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    activity?.runOnUiThread {
                        image_loading.visibility = View.GONE
                        profile_image.setPadding(150.dp,150.dp,150.dp,150.dp)
                        update_profile_image.text = "Set profile image"
                    }
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    activity?.runOnUiThread {
                        image_loading.visibility = View.GONE
                        profile_image.setPadding(0,0,0,0)
                        profile_image.setImageDrawable(resource)
                        update_profile_image.text = "Set profile image"
                    }
                    return false
                }
            }).submit()


    }

    private fun animateContainersIn() {
        Timeline.createParallel()
            .push(
                Timeline.createParallel()
                    .push(Tween.to(account_image_container, Alpha.VIEW, 1.0f).target(1.0f))
                    .push(Tween.to(account_image_container, Translation.XY).target(-370.dp.toFloat(), 0f).ease(Cubic.INOUT).duration(1.0f))
            )
            .push(
                Timeline.createParallel()
                    .pushPause(0.3f)
                    .push(Tween.to(account_info_container, Alpha.VIEW, 1.0f).target(1.0f))
                    .push(Tween.to(account_info_container, Translation.XY).target(370.dp.toFloat(), 0f).ease(Cubic.INOUT).duration(1.0f))
            )
            .push(
                Timeline.createParallel()
                    .pushPause(0.5f)
                    .push(Tween.to(status_container, Alpha.VIEW, 1.0f).target(1.0f))
                    .push(Tween.to(status_container, Translation.XY).target(-370.dp.toFloat(), 0f).ease(Cubic.INOUT).duration(1.0f))
            )
            .push(
                Timeline.createParallel()
                    .pushPause(0.7f)
                    .push(Tween.to(adview_container, Alpha.VIEW, 1.0f).target(1.0f))
                    .push(Tween.to(adview_container, Translation.XY).target(370.dp.toFloat(), 0f).ease(Cubic.INOUT).duration(1.0f))
            )
            .start(ViewTweenManager.get(account_image_container))

    }
}
