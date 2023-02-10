package pe.com.carwashperuapp.carwashapp.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.database.horario.HorarioConfig
import java.util.*

@JsonClass(generateAdapter = true)
class RespHorarioConfig(
    statusCode: Int,
    httpStatus: String,
    message: String,
    timeStamp: String,
    @Json(name = "data")
    val data: Data
) : Response(statusCode, httpStatus, message, timeStamp) {
    @JsonClass(generateAdapter = true)
    class Data(
        @Json(name = "horarioConfigs") var horarioConfigs: List<HorarioConfig>,
    )
}