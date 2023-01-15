package pe.com.carwashperuapp.carwashapp.network.response

import com.squareup.moshi.Json
import java.util.Date

open class Response(
    @Json(name = "statusCode")
    val statusCode: Int,
    @Json(name = "httpStatus")
    val httpStatus: String,
    @Json(name = "message")
    val message: String,
    @Json(name = "timeStamp")
    val timeStamp: Date,
)