package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json

class ReqAnuncioEliminar(
    @Json(name = "ids") var ids: List<Int>
)