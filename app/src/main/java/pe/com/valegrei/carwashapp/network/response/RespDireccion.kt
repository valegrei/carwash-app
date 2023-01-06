package pe.com.valegrei.carwashapp.network.response

import com.squareup.moshi.Json
import pe.com.valegrei.carwashapp.database.direccion.Direccion
import java.util.*

class RespDireccion(
    statusCode: Int,
    httpStatus: String,
    message: String,
    timeStamp: Date,
    @Json(name = "data")
    val data: Data
) : Response(statusCode, httpStatus, message, timeStamp) {
    class Data(
        @Json(name = "direcciones") var direcciones: List<Direccion>,
    )
}