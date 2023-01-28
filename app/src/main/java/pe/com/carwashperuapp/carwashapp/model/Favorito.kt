package pe.com.carwashperuapp.carwashapp.model

import com.squareup.moshi.Json

class Favorito(
    @Json(name="id") val id: Int,
    @Json(name="idCliente") val idCliente: Int,
    @Json(name="idLocal") val idLocal: Int,
    @Json(name="estado") val estado: Boolean,
)