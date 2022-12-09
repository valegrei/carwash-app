package pe.com.valegrei.carwashapp.ui

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.database.SesionData

@BindingAdapter("checkStatus")
fun bindCheckStatus(checkImageView: ImageView, checked: Boolean) {
    if (checked) {
        checkImageView.setImageResource(R.drawable.ic_check_circle_green_24)
    } else {
        checkImageView.setImageResource(R.drawable.ic_check_circle_grey_24)
    }
}

/**
 * Uses the Coil library to load an image by URL into an [ImageView]
 */
@BindingAdapter("roudendImageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val sesion = SesionData(imgView.context).getCurrentSesion()
        imgView.load(it) {
            setHeader("Authorization", sesion?.getTokenBearer()!!)
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
            transformations(CircleCropTransformation())
        }
    }
}