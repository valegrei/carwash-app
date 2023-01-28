package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json

class ReqFavorito(
    @Json(name = "idLocal") val idLocal: Int,
)