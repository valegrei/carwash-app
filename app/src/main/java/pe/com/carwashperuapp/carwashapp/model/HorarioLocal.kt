package pe.com.carwashperuapp.carwashapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.ui.util.formatHora

@JsonClass(generateAdapter = true)
data class HorarioLocal(
    @Json(name = "id") val id: Int,
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
) {
    fun dias(): String {
        var dias = ""
        if (lunes && martes && miercoles && jueves && viernes && sabado && domingo) {
            dias = "Todos los días"
        } else if (lunes && martes && miercoles && jueves && viernes) {
            dias = "Lun - Vie"
        } else {
            dias += if (lunes) "Lun " else ""
            dias += if (martes) "Mar " else ""
            dias += if (miercoles) "Mie " else ""
            dias += if (jueves) "Jue " else ""
            dias += if (viernes) "Vie " else ""
            dias += if (sabado) "Sab " else ""
            dias += if (domingo) "Dom " else ""
        }
        return dias
    }

    fun horarioIni(): String = formatHora(horaIni, minIni)
    fun horarioFin(): String = formatHora(horaFin, minFin)
    fun horario(): String = "${horarioIni()} - ${horarioFin()}"
    fun resumen(): String = "${dias()}\n${horario()}"
}