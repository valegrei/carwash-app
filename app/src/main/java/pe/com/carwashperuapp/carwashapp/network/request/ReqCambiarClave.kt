package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json

class ReqCambiarClave(
    @Json(name = "correo") var correo: String,
    @Json(name = "clave") var clave: String,
    @Json(name = "codigo") var codigo: Int
)