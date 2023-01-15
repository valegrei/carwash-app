package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json

class ReqHorarioConfig(
    @Json(name = "lunes") val lunes: Boolean,
    @Json(name = "martes") val martes: Boolean,
    @Json(name = "miercoles") val miercoles: Boolean,
    @Json(name = "jueves") val jueves: Boolean,
    @Json(name = "viernes") val viernes: Boolean,
    @Json(name = "sabado") val sabado: Boolean,
    @Json(name = "domingo") val domingo: Boolean,
    @Json(name = "horaIni") val horaIni: Int,
    @Json(name = "minIni") val minIni: Int,
    @Json(name = "horaFin") val horaFin: Int,
    @Json(name = "minFin") val minFin: Int,
    @Json(name = "intervalo") val intervalo: Int,
    @Json(name = "idLocal") val idLocal: Int,
)