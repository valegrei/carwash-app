package pe.com.valegrei.carwashapp.network.request

import android.icu.math.BigDecimal
import com.squareup.moshi.Json

class ReqAddServicio(
    @Json(name = "nombre") var nombre: String,
    @Json(name = "precio") var precio: BigDecimal,
)