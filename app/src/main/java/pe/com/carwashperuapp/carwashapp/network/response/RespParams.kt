package pe.com.carwashperuapp.carwashapp.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.database.parametro.Parametro
import java.util.*

@JsonClass(generateAdapter = true)
class RespParams(
    statusCode: Int,
    httpStatus: String,
    message: String,
    timeStamp: Date,
    @Json(name = "data")
    val data: Data
) : Response(statusCode, httpStatus, message, timeStamp) {
    @JsonClass(generateAdapter = true)
    class Data(
        @Json(name = "parametros") var parametros: List<Parametro>,
    )
}