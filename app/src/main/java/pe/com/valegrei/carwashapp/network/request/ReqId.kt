package pe.com.valegrei.carwashapp.network.request

import com.squareup.moshi.Json

class ReqId(
    @Json(name = "id") var id: Int
)