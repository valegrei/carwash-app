package pe.com.valegrei.carwashapp.network.response

import com.squareup.moshi.Json

class RespError(
    @Json(name = "message")
    val message: String = "",
)