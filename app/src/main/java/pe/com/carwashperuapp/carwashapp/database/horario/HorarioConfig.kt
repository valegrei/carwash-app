package pe.com.carwashperuapp.carwashapp.database.horario

import androidx.room.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.database.direccion.Direccion
import pe.com.carwashperuapp.carwashapp.ui.util.formatHora

@JsonClass(generateAdapter = true)
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
    @Json(name = "nroAtenciones") @ColumnInfo(name = "nroAtenciones") val nroAtenciones: Int,
    @Json(name = "estado") @ColumnInfo(name = "estado") val estado: Boolean,
    @Json(name = "idDistrib") @ColumnInfo(name = "idDistrib") val idDistrib: Int,
    @Json(name = "idLocal") @ColumnInfo(name = "idLocal") val idLocal: Int,
) {
    fun dias(): String {
        if (lunes && martes && miercoles && jueves && viernes && sabado && domingo) {
            return "Todos los días"
        }
        if (lunes && martes && miercoles && jueves && viernes && !sabado) {
            return "Lun - Vie"
        }
        if (lunes && martes && miercoles && jueves && viernes && sabado) {
            return "Lun - Sab"
        }
        var dias = ""
        dias += if (lunes) "Lun " else ""
        dias += if (martes) "Mar " else ""
        dias += if (miercoles) "Mie " else ""
        dias += if (jueves) "Jue " else ""
        dias += if (viernes) "Vie " else ""
        dias += if (sabado) "Sab " else ""
        dias += if (domingo) "Dom " else ""
        return dias
    }

    fun horarioIni(): String = formatHora(horaIni, minIni)
    fun horarioFin(): String = formatHora(horaFin, minFin)
    fun horario(): String = "${horarioIni()} - ${horarioFin()}"
    fun resumen(): String = "${dias()}\nInicio - Fin: ${horario()}\nNro. Atenciones: $nroAtenciones"
    fun atenciones(): String = "$nroAtenciones"
}

data class HorarioConfigLocal(
    @Embedded val horarioConfig: HorarioConfig,
    @Relation(
        parentColumn = "idLocal",
        entityColumn = "id"
    )
    val local: Direccion
) {
    fun resumen(): String = "${local.direccion}\n${horarioConfig.resumen()}"
}