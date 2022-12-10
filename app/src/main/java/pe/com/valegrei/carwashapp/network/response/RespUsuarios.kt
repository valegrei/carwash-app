package pe.com.valegrei.carwashapp.network.response

import com.squareup.moshi.Json
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import java.util.Date

class RespUsuarios(
    statusCode: Int,
    httpStatus: String,
    message: String,
    timeStamp: Date,
    @Json(name = "data") val data: Data
) : Response(statusCode, httpStatus, message, timeStamp) {
    class Data(@Json(name = "usuarios") var usuarios: List<Usuario>)
}