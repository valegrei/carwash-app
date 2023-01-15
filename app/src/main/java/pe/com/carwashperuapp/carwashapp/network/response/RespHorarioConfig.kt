package pe.com.carwashperuapp.carwashapp.network.response

import com.squareup.moshi.Json
import pe.com.carwashperuapp.carwashapp.database.horario.HorarioConfig
import java.util.*

class RespHorarioConfig(
    statusCode: Int,
    httpStatus: String,
    message: String,
    timeStamp: Date,
    @Json(name = "data")
    val data: Data
) : Response(statusCode, httpStatus, message, timeStamp) {
    class Data(
        @Json(name = "horarioConfigs") var horarioConfigs: List<HorarioConfig>,
    )
}