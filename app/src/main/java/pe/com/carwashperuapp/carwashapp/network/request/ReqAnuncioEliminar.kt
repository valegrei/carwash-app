package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ReqAnuncioEliminar(
    @Json(name = "ids") var ids: List<Int>
)