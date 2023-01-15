package pe.com.carwashperuapp.carwashapp.network.response

import com.squareup.moshi.Json
import pe.com.carwashperuapp.carwashapp.database.usuario.Usuario
import java.util.Date

class RespUsuario(
    statusCode: Int,
    httpStatus: String,
    message: String,
    timeStamp: Date,
    @Json(name = "data") val data: Data
) : Response(statusCode, httpStatus, message, timeStamp) {
    class Data(@Json(name = "usuario") var usuario: Usuario)
}