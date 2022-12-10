package pe.com.valegrei.carwashapp.network.request

import com.squareup.moshi.Json

class ReqVerificarCorreo(
    @Json(name = "id") var id: Int,
    @Json(name = "codigo") var codigo: Int
)