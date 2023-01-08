package pe.com.valegrei.carwashapp.database.horario

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import pe.com.valegrei.carwashapp.ui.util.formatHora

@Entity(tableName = "horario_config")
data class HorarioConfig(
    @Json(name = "id") @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @Json(name = "lunes") @ColumnInfo(name = "lunes") val lunes: Boolean,
    @Json(name = "martes") @ColumnInfo(name = "martes") val martes: Boolean,
    @Json(name = "miercoles") @ColumnInfo(name = "miercoles") val miercoles: Boolean,
    @Json(name = "jueves") @ColumnInfo(name = "jueves") val jueves: Boolean,
    @Json(name = "viernes") @ColumnInfo(name = "viernes") val viernes: Boolean,
    @Json(name = "sabado") @ColumnInfo(name = "sabado") val sabado: Boolean,
    @Json(name = "domingo") @ColumnInfo(name = "domingo") val domingo: Boolean,
    @Json(name = "horaIni") @ColumnInfo(name = "horaIni") val horaIni: Int,
    @Json(name = "minIni") @ColumnInfo(name = "minIni") val minIni: Int,
    @Json(name = "horaFin") @ColumnInfo(name = "horaFin") val horaFin: Int,
    @Json(name = "minFin") @ColumnInfo(name = "minFin") val minFin: Int,
    @Json(name = "intervalo") @ColumnInfo(name = "intervalo") val intervalo: Int,
    @Json(name = "estado") @ColumnInfo(name = "estado") val estado: Boolean,
    @Json(name = "idDistrib") @ColumnInfo(name = "idDistrib") val idDistrib: Int,
    @Json(name = "idLocal") @ColumnInfo(name = "idLocal") val idLocal: Int,
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
    fun intervaloMin(): String = "$intervalo min"
    fun resumen(): String = "${dias()}\nInicio - Fin: ${horario()}\nIntervalo: ${intervaloMin()}"
}