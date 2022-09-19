package gr.gkortsaridis.gatekeeper.UI.Account


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AnalyticsRepository
import gr.gkortsaridis.gatekeeper.UI.Composables.GateKeeperTextField
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperShapes
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme

class MyAccountFragment : Fragment() {

    private lateinit var viewDialog: ViewDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent { accountPage() }
        }
    }

    @Preview
    @Composable
    fun accountPage() {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(GateKeeperTheme.light_grey)
        ) {
            userImgCard(modifier = Modifier.align(Alignment.End))
            userDetailsCard()
            userExtraInfoCard(modifier = Modifier.align(Alignment.End))
        }
    }

    @Composable
    fun userImgCard(
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 32.dp).composed { modifier },
            horizontalAlignment = Alignment.End
        ) {
            Card(
                modifier = Modifier.size(width = 370.dp, height = 300.dp),
                elevation = 4.dp,
                backgroundColor = GateKeeperTheme.busy_grey,
                shape = GateKeeperShapes.getLeftRadiusCard(200)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Add Profile Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(120.dp)
                )
            }

            Text(
                text = "UPDATE PROFILE IMAGE",
                modifier = Modifier.padding(all=16.dp),
                color = GateKeeperTheme.colorPrimaryDark
            )

        }
    }

    @Composable
    fun userDetailsCard() {
        Column(
            modifier = Modifier.padding(vertical = 32.dp),
        ) {
            Card(
                modifier = Modifier.width(width = 370.dp),
                elevation = 4.dp,
                backgroundColor = GateKeeperTheme.white,
                shape = GateKeeperShapes.getRightRadiusCard(200)
            ) {
                Column() {
                    GateKeeperTextField(
                        placeholder="Full Name",
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 24.dp, bottom = 8.dp)
                    )

                    GateKeeperTextField(
                        placeholder="Email",
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 24.dp, bottom = 32.dp)
                    )

                }
             }

            Text(
                text = "UPDATE ACCOUNT DETAILS",
                modifier = Modifier.padding(all=16.dp),
                color = GateKeeperTheme.colorPrimaryDark
            )

        }
    }

    @Composable
    fun userExtraInfoCard(
        modifier: Modifier = Modifier
    ) {

        Column(
            modifier = Modifier
                .padding(vertical = 32.dp)
                .composed { modifier },
        ) {
            Card(
                modifier = Modifier.size(width = 370.dp, height = 1000.dp),
                elevation = 4.dp,
                backgroundColor = GateKeeperTheme.white,
                shape = GateKeeperShapes.getLeftRadiusCard(150)
            ) {
                Row(
                    modifier = Modifier.weight(2F)
                ) {
                    userExtraInfoButton(
                        modifier = Modifier.weight(1F),
                        img = R.drawable.account_grey,
                        text = "Account History",
                        onClick = { goToHistory() }
                    )
                    userExtraInfoButton(
                        modifier = Modifier.weight(1F),
                        img = R.drawable.cash,
                        text = "Billing Status",
                        onClick = { goToBilling() }
                    )
                }
            }

        }

    }

    @Composable
    fun userExtraInfoButton(
        modifier: Modifier = Modifier,
        img: Int,
        text: String,
        onClick: () -> Unit = {},
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clickable { onClick }
                .composed { modifier },
            contentAlignment = Alignment.Center,

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    modifier = Modifier.size(width = 30.dp, height = 30.dp),
                    painter = painterResource(id = img),
                    contentDescription = text
                )
                Text(
                    text = text,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

        }
    }

    private fun goToHistory() {
        AnalyticsRepository.trackEvent(AnalyticsRepository.HISTORY_PAGE)
        startActivity(Intent(requireActivity(), AccountHistoryActivity::class.java))
    }

    private fun goToBilling() {
        AnalyticsRepository.trackEvent(AnalyticsRepository.BILLING_PAGE)
        Toast.makeText(context, "Everything is free for now :)", Toast.LENGTH_SHORT).show()
        //context?.startActivity(Intent(activity, AccountStatusActivity::class.java))
    }

    /*
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
    */

    /*


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
     */

}
