package pe.com.carwashperuapp.carwashapp.model

import com.squareup.moshi.Json
import pe.com.carwashperuapp.carwashapp.ui.util.formatHora

data class Horario(
    @Json(name = "id") val id: Int,
    @Json(name = "fecha") val fecha: String,
    @Json(name = "horaIni") val horaIni: String,
    @Json(name = "horaFin") val horaFin: String,
    @Json(name = "Local") val local: Local? = null,
    @Json(name = "Distrib") val distrib: Distrib? = null,
    var selected: Boolean? = false,
) {
    override fun toString(): String {
        return "${formatHora(horaIni)} - ${formatHora(horaFin)}"
    }
}