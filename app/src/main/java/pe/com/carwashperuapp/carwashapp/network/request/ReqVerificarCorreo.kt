package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ReqVerificarCorreo(
    @Json(name = "id") var id: Int,
    @Json(name = "codigo") var codigo: Int
)