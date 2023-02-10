package pe.com.carwashperuapp.carwashapp.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.database.usuario.Usuario
import java.util.Date

@JsonClass(generateAdapter = true)
class RespUsuario(
    statusCode: Int,
    httpStatus: String,
    message: String,
    timeStamp: String,
    @Json(name = "data") val data: Data
) : Response(statusCode, httpStatus, message, timeStamp) {
    @JsonClass(generateAdapter = true)
    class Data(@Json(name = "usuario") var usuario: Usuario)
}