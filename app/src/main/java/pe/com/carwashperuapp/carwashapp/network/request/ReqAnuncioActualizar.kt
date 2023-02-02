package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ReqAnuncioActualizar(
    @Json(name = "descripcion") var descripcion: String?,
    @Json(name = "url") var url: String?,
    @Json(name = "mostrar") var mostrar: Boolean?,
)