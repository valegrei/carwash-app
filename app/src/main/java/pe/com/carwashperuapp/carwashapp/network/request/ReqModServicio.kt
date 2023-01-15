package pe.com.carwashperuapp.carwashapp.network.request

import android.icu.math.BigDecimal
import com.squareup.moshi.Json

class ReqModServicio(
    @Json(name = "id") var id: Int,
    @Json(name = "nombre") var nombre: String,
    @Json(name = "precio") var precio: BigDecimal,
    @Json(name = "estado") var estado: Boolean,
)