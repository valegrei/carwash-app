package pe.com.carwashperuapp.carwashapp.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.model.Reserva
import java.util.*

@JsonClass(generateAdapter = true)
class RespReservas(
    statusCode: Int,
    httpStatus: String,
    message: String,
    timeStamp: String,
    @Json(name = "data")
    val data: Data
) : Response(statusCode, httpStatus, message, timeStamp) {
    @JsonClass(generateAdapter = true)
    class Data(
        @Json(name = "reservas") var reservas: List<Reserva>,
    )
}