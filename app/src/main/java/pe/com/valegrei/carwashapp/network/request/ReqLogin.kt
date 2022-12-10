package pe.com.valegrei.carwashapp.network.request

import com.squareup.moshi.Json

class ReqLogin(
    @Json(name = "correo") var correo: String,
    @Json(name = "clave") var clave: String
)