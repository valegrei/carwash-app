package pe.com.carwashperuapp.carwashapp.model

import androidx.room.ColumnInfo
import pe.com.carwashperuapp.carwashapp.ui.util.formatHora

data class Horario(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "fecha") val fecha: String,
    @ColumnInfo(name = "horaIni") val horaIni: String,
    @ColumnInfo(name = "horaFin") val horaFin: String,
    @ColumnInfo(name = "estado") val estado: Boolean,
    @ColumnInfo(name = "idDistrib") val idDistrib: Int,
    @ColumnInfo(name = "idLocal") val idLocal: Int,
    @ColumnInfo(name = "idHorarioConfig") val idHorarioConfig: Int,
    var selected: Boolean = false,
) {
    override fun toString(): String {
        return "${formatHora(horaIni)} - ${formatHora(horaFin)}"
    }
}