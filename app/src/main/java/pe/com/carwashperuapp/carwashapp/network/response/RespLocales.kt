package pe.com.carwashperuapp.carwashapp.network.response

import com.squareup.moshi.Json
import pe.com.carwashperuapp.carwashapp.model.Local
import java.util.*

class RespLocales(
    statusCode: Int,
    httpStatus: String,
    message: String,
    timeStamp: Date,
    @Json(name = "data")
    val data: Data
) : Response(statusCode, httpStatus, message, timeStamp) {
    class Data(
        @Json(name = "locales") var locales: List<Local>,
    )
}