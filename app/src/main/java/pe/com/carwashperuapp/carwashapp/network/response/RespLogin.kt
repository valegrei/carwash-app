package pe.com.carwashperuapp.carwashapp.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.database.usuario.Usuario
import java.util.*

@JsonClass(generateAdapter = true)
class RespLogin(
    statusCode: Int,
    httpStatus: String,
    message: String,
    timeStamp: Date,
    @Json(name = "data")
    val data: Data
) : Response(statusCode, httpStatus, message, timeStamp) {
    @JsonClass(generateAdapter = true)
    class Data(
        @Json(name = "usuario") var usuario: Usuario,
        @Json(name = "exp") var exp: Date?,
        @Json(name = "jwt") var jwt: String?
    )
}