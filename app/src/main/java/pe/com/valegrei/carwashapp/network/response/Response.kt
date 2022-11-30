package pe.com.valegrei.carwashapp.network.response

import com.squareup.moshi.Json

open class Response(
    @Json(name = "statusCode")
    val statusCode: Int,
    @Json(name = "httpStatus")
    val httpStatus: String,
    @Json(name = "message")
    val message: String,
    @Json(name = "timeStamp")
    val timeStamp: String,
)