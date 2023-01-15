package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json

class ReqAnuncioActualizar(
    @Json(name = "descripcion") var descripcion: String?,
    @Json(name = "url") var url: String?,
    @Json(name = "mostrar") var mostrar: Boolean?,
)