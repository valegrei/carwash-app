package pe.com.valegrei.carwashapp.network.response

import com.squareup.moshi.Json
import pe.com.valegrei.carwashapp.database.servicio.Servicio
import java.util.Date

class RespServicios(
    statusCode: Int,
    httpStatus: String,
    message: String,
    timeStamp: Date,
    @Json(name = "data")
    val data: Data
) : Response(statusCode, httpStatus, message, timeStamp) {
    class Data(
        @Json(name = "servicios") var servicios: List<Servicio>,
    )
}