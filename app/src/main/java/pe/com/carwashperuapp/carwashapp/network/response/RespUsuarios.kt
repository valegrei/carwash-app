package pe.com.carwashperuapp.carwashapp.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.database.usuario.Usuario
import java.util.Date

@JsonClass(generateAdapter = true)
class RespUsuarios(
    statusCode: Int,
    httpStatus: String,
    message: String,
    timeStamp: Date,
    @Json(name = "data") val data: Data
) : Response(statusCode, httpStatus, message, timeStamp) {
    @JsonClass(generateAdapter = true)
    class Data(@Json(name = "usuarios") var usuarios: List<Usuario>)
}