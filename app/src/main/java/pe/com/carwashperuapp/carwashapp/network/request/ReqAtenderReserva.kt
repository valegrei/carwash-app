package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.model.ServicioReserva

@JsonClass(generateAdapter = true)
class ReqAtenderReserva(
    @Json(name = "servicios") val servicios: List<ServicioReserva>,
)