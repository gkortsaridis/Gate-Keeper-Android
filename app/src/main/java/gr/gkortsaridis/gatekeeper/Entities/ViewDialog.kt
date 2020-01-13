package gr.gkortsaridis.gatekeeper.Entities

import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import gr.gkortsaridis.gatekeeper.R


class ViewDialog {
    private var activity: Activity
    private var dialog: Dialog? = null

    constructor(activity: Activity){
        this.activity = activity
    }

    fun showDialog() {
        dialog = Dialog(activity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //...set cancelable false so that it's never get hidden
        dialog?.setCancelable(false)
        //...that's the layout i told you will inflate later
        dialog?.setContentView(R.layout.loading_layout)

        //...initialize the imageView form infalted layout
        val gifImageView : ImageView = dialog?.findViewById(R.id.custom_loading_image)!!

        /*
        it was never easy to load gif into an ImageView before Glide or Others library
        and for doing this we need DrawableImageViewTarget to that ImageView
        */
        val imageViewTarget = GlideDrawableImageViewTarget(gifImageView)

        //...now load that gif which we put inside the drawble folder here with the help of Glide

        Glide.with(activity)
            .load(R.drawable.loading)
            .placeholder(R.drawable.loading)
            .centerCrop()
            .crossFade()
            .into(imageViewTarget)

        //...finaly show it
        dialog?.show()
    }

    //..also create a method which will hide the dialog when some work is done
    fun hideDialog() {
        dialog?.dismiss()
    }

}