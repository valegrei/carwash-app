package pe.com.valegrei.carwashapp.network.request

import com.squareup.moshi.Json

class ReqAnuncioEliminar(
    @Json(name = "ids") var ids: List<Int>
)