package pe.com.valegrei.carwashapp.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import pe.com.valegrei.carwashapp.EstrEditFoto
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.ui.announcement.TuplaImageEdit

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
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val sesion = SesionData(imgView.context).getCurrentSesion()
        imgView.load(it) {
            setHeader("Authorization", sesion?.getTokenBearer()!!)
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}

/**
 * Uses the Coil library to load an image by URL into an [ImageView]
 */
@BindingAdapter("roudendImageUrl")
fun bindRoundImage(imgView: ImageView, imgUrl: String?) {
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

/**
 * Uses the Coil library to load an image by URL into an [ImageView]
 */
@BindingAdapter("roudendImageEdit")
fun bindRoundImageEdit(imgView: ImageView, editFoto: EstrEditFoto?) {
    editFoto?.let {
        if (it.eliminarFoto) {
            imgView.load(R.drawable.logo)
            return
        }
        if (it.uriFile != null) {
            imgView.load(it.uriFile) {
                transformations(CircleCropTransformation())
            }
            return
        }
        if ((it.urlOriginal ?: "").isNotEmpty()) {
            val sesion = SesionData(imgView.context).getCurrentSesion()
            imgView.load(it.urlOriginal) {
                setHeader("Authorization", sesion?.getTokenBearer()!!)
                placeholder(R.drawable.loading_animation)
                transformations(CircleCropTransformation())
            }
        } else {
            imgView.load(R.drawable.logo)
        }
    }
}

@BindingAdapter("imageUrlEdit")
fun bindImageEdit(imgView: ImageView, imageEdit: TuplaImageEdit?) {
    imageEdit?.let {
        if ((it.urlImagen ?: "").isNotEmpty()) {
            val sesion = SesionData(imgView.context).getCurrentSesion()
            imgView.load(it.urlImagen) {
                setHeader("Authorization", sesion?.getTokenBearer()!!)
                placeholder(R.drawable.loading_animation)
            }
            return
        }
        if (it.uriFile != null) {
            imgView.load(it.uriFile)
        }
    }
}