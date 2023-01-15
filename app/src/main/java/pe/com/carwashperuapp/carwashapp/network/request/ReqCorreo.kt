package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json

class ReqCorreo(
    @Json(name = "correo") var correo: String
)