package pe.com.carwashperuapp.carwashapp.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.card.MaterialCardView
import pe.com.carwashperuapp.carwashapp.EstrEditFoto
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.direccion.TipoDireccion
import pe.com.carwashperuapp.carwashapp.database.usuario.EstadoUsuario
import pe.com.carwashperuapp.carwashapp.database.usuario.TipoUsuario
import pe.com.carwashperuapp.carwashapp.model.ReservaEstado
import pe.com.carwashperuapp.carwashapp.model.ServicioEstado
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

@BindingAdapter("imageUrlEdit2")
fun bindImageEdit2(imgView: ImageView, imageEdit: TuplaImageEdit?) {
    imageEdit?.let {
        if ((it.urlImagen ?: "").isNotEmpty()) {
            val sesion = SesionData(imgView.context).getCurrentSesion()
            imgView.load(it.urlImagen) {
                setHeader("Authorization", sesion?.getTokenBearer()!!)
                placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_broken_image)
            }
            return
        }
        if (it.uriFile != null) {
            imgView.load(it.uriFile)
            return
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

@BindingAdapter("tipoDir")
fun bindTipoDir(textView: TextView, tipoDir: Int?) {
    val resIcon = when (tipoDir) {
        TipoDireccion.LOCAL.id -> R.drawable.baseline_store_24
        TipoDireccion.CASA.id -> R.drawable.baseline_house_24
        TipoDireccion.OFICINA.id -> R.drawable.outline_business_24
        else -> R.drawable.ic_baseline_place_24
    }
    textView.setCompoundDrawablesWithIntrinsicBounds(resIcon, 0, 0, 0);
}

@BindingAdapter("tipoDir")
fun bindTipoDir(imageView: ImageView, tipoDir: Int?) {
    val resIcon = when (tipoDir) {
        TipoDireccion.LOCAL.id -> R.drawable.baseline_store_24
        TipoDireccion.CASA.id -> R.drawable.baseline_house_24
        TipoDireccion.OFICINA.id -> R.drawable.outline_business_24
        else -> R.drawable.ic_baseline_place_24
    }
    imageView.setImageResource(resIcon)
}

@BindingAdapter("estadoServColor")
fun bindEstadoServ(textView: TextView, estado: Int?) {
    val context = textView.context
    var resColor = when (estado) {
        ServicioEstado.NO_ATENDIDO.id -> {
            R.color.service_state_pending
        }
        ServicioEstado.ATENDIDO.id -> {
            R.color.service_state_attended
        }
        ServicioEstado.ANULADO.id -> {
            R.color.service_state_canceled
        }
        else -> {
            R.color.service_state_pending
        }
    }
    textView.setTextColor(context.getColor(resColor))
}

@BindingAdapter("estadoReservaColor")
fun bindEstadoReserva(cardView: MaterialCardView, estado: Int?) {
    val context = cardView.context
    var resColor = when (estado) {
        ReservaEstado.NO_ATENDIDO.id -> {
            R.color.service_state_pending_bg
        }
        ReservaEstado.ATENDIDO.id -> {
            R.color.service_state_attended_bg
        }
        ReservaEstado.ATENDIENDO.id -> {
            R.color.service_state_attendeding_bg
        }
        ReservaEstado.ANULADO.id -> {
            R.color.service_state_canceled_bg
        }
        else -> {
            R.color.service_state_pending_bg
        }
    }
    cardView.setCardBackgroundColor(context.getColor(resColor))
}

fun bindImageBanner(imgView: ImageView, imgUrl: String?) {
    val sesion = SesionData(imgView.context).getCurrentSesion()
    if (!(imgUrl).isNullOrEmpty()) {
        imgView.visibility = View.VISIBLE
        imgView.load(imgUrl) {
            setHeader("Authorization", sesion?.getTokenBearer()!!)
        }
    } else {
        //imgView.setImageDrawable(null)
        imgView.visibility = View.GONE
    }
}

fun bindImageEdit(imgBanner: ImageView, editFoto: EstrEditFoto?) {
    editFoto?.let {
        if (it.eliminarFoto) {
            imgBanner.visibility = View.GONE //setImageDrawable(null)
            return
        }
        if (it.uriFile != null) {
            imgBanner.visibility = View.VISIBLE
            imgBanner.load(it.uriFile)
            return
        }
        if ((it.urlOriginal ?: "").isNotEmpty()) {
            imgBanner.visibility = View.VISIBLE
            val sesion = SesionData(imgBanner.context).getCurrentSesion()
            imgBanner.load(it.urlOriginal) {
                setHeader("Authorization", sesion?.getTokenBearer()!!)
            }
        } else {
            imgBanner.visibility = View.GONE
        }
    }
}