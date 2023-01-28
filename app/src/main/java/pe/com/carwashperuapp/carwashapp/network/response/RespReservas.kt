package pe.com.carwashperuapp.carwashapp.network.response

import com.squareup.moshi.Json
import pe.com.carwashperuapp.carwashapp.model.Horario
import pe.com.carwashperuapp.carwashapp.model.Reserva
import java.util.*

class RespReservas(
    statusCode: Int,
    httpStatus: String,
    message: String,
    timeStamp: Date,
    @Json(name = "data")
    val data: Data
) : Response(statusCode, httpStatus, message, timeStamp) {
    class Data(
        @Json(name = "reservas") var reservas: List<Reserva>,
    )
}