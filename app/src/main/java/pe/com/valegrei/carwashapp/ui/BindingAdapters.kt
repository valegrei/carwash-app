package pe.com.valegrei.carwashapp.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.gms.maps.model.LatLng
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.direccion.Direccion
import pe.com.valegrei.carwashapp.database.usuario.EstadoUsuario
import pe.com.valegrei.carwashapp.database.usuario.TipoUsuario
import pe.com.valegrei.carwashapp.ui.announcement.TuplaImageEdit
import pe.com.valegrei.carwashapp.ui.util.UrlSigner
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