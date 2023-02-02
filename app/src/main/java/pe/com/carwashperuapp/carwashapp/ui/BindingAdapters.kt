package pe.com.carwashperuapp.carwashapp.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.gms.maps.model.LatLng
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.direccion.TipoDireccion
import pe.com.carwashperuapp.carwashapp.database.usuario.EstadoUsuario
import pe.com.carwashperuapp.carwashapp.database.usuario.TipoUsuario
import pe.com.carwashperuapp.carwashapp.ui.announcement.TuplaImageEdit
import pe.com.carwashperuapp.carwashapp.ui.util.UrlSigner
import java.net.URL

@BindingAdapter("checkStatus")
fun bindCheckStatus(checkImageView: ImageView, estadoUsuario: Int) {
    when (estadoUsuario) {
        EstadoUsuario.ACTIVO.id -> checkImageView.setImageResource(R.drawable.ic_circle_green_24)
        EstadoUsuario.VERIFICANDO.id -> checkImageView.setImageResource(R.drawable.ic_circle_yellow_24)
        EstadoUsuario.INACTIVO.id -> checkImageView.setImageResource(R.drawable.ic_circle_grey_24)
    }
}

@BindingAdapter("userType")
fun bindCheckStatus(textView: TextView, tipoUsuario: Int) {
    when (tipoUsuario) {
        TipoUsuario.ADMIN.id -> textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_baseline_setting_purple_24,
            0,
            0,
            0
        )
        TipoUsuario.CLIENTE.id -> textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_baseline_person_purple_24,
            0,
            0,
            0
        )
        TipoUsuario.DISTR.id -> textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_baseline_store_purple_24,
            0,
            0,
            0
        )
    }
}

/**
 * Uses the Coil library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    val sesion = SesionData(imgView.context).getCurrentSesion()
    if (!(imgUrl).isNullOrEmpty()) {
        imgView.colorFilter = null
        imgView.scaleType = ImageView.ScaleType.CENTER_CROP
        imgView.load(imgUrl) {
            setHeader("Authorization", sesion?.getTokenBearer()!!)
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    } else {
        imgView.setColorFilter(imgView.context.getColor(R.color.purple))
        imgView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        imgView.load(R.drawable.ic_splash)
    }
}


/**
 * Uses the Coil library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrlGallery")
fun bindImageGallery(imgView: ImageView, imgUrl: String?) {
    val sesion = SesionData(imgView.context).getCurrentSesion()
    if (!(imgUrl).isNullOrEmpty()) {
        imgView.load(imgUrl) {
            setHeader("Authorization", sesion?.getTokenBearer()!!)
        }
    }
}


/**
 * Uses the Coil library to load an image by URL into an [ImageView]
 */
@BindingAdapter("roudendImageUrl")
fun bindRoundImage(imgView: ImageView, imgUrl: String?) {
    val sesion = SesionData(imgView.context).getCurrentSesion()
    if (!(imgUrl).isNullOrEmpty()) {
        imgView.colorFilter = null
        imgView.scaleType = ImageView.ScaleType.CENTER_CROP
        imgView.load(imgUrl) {
            setHeader("Authorization", sesion?.getTokenBearer()!!)
            transformations(CircleCropTransformation())
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    } else {
        imgView.setColorFilter(imgView.context.getColor(R.color.purple))
        imgView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        imgView.load(R.drawable.logo)
    }
}

@BindingAdapter("imageUrlEdit")
fun bindImageEdit(imgView: ImageView, imageEdit: TuplaImageEdit?) {
    imageEdit?.let {
        if ((it.urlImagen ?: "").isNotEmpty()) {
            imgView.colorFilter = null
            imgView.scaleType = ImageView.ScaleType.CENTER_CROP
            val sesion = SesionData(imgView.context).getCurrentSesion()
            imgView.load(it.urlImagen) {
                setHeader("Authorization", sesion?.getTokenBearer()!!)
                placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_broken_image)
            }
            return
        }
        if (it.uriFile != null) {
            imgView.colorFilter = null
            imgView.scaleType = ImageView.ScaleType.CENTER_CROP
            imgView.load(it.uriFile)
            return
        }

        imgView.setColorFilter(imgView.context.getColor(R.color.purple))
        imgView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        imgView.load(R.drawable.ic_splash)
    }
}

/**
 * Uses the Coil library to load an image by URL into an [ImageView]
 */
@BindingAdapter("staticMapDireccion")
fun bindStaticMapDireccion(imgView: ImageView, latLng: LatLng?) {
    latLng?.let {
        val secretKey = imgView.context.getString(R.string.secret_key)
        val apiKey = imgView.context.getString(R.string.api_key)
        val url = URL(
            imgView.context.getString(
                R.string.map_static_url,
                latLng.latitude,
                latLng.longitude,
                apiKey
            )
        )
        val urlSigner = UrlSigner(secretKey)
        val request = urlSigner.signRequest(url.path, url.query)
        val mapUrl = url.protocol + "://" + url.host + request
        imgView.load(mapUrl) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}

@BindingAdapter("tipoDir")
fun bindTipoDir(textView: TextView, tipoDir: Int?) {
    val resIcon = when (tipoDir) {
        TipoDireccion.LOCAL.id -> R.drawable.ic_store_black_24
        TipoDireccion.CASA.id -> R.drawable.outline_house_24
        TipoDireccion.OFICINA.id -> R.drawable.outline_business_24
        else -> R.drawable.ic_outline_place_24
    }
    textView.setCompoundDrawablesWithIntrinsicBounds(resIcon, 0, 0, 0);
}
