package pe.com.carwashperuapp.carwashapp.ui.util

import android.icu.math.BigDecimal
import android.icu.text.DecimalFormat
import pe.com.carwashperuapp.carwashapp.model.Horario
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

fun formatHora(hora: Int, min: Int): String {
    val minStr = if (min.toString().length < 2) "0$min" else min.toString()
    return if (hora <= 12) {
        val horaStr = if (hora.toString().length < 2) "0$hora" else hora.toString()
        when (hora) {
            0 -> "12:$minStr am"
            12 -> "12:$minStr pm"
            else -> "$horaStr:$minStr am"
        }
    } else {
        val horaStr =
            if ((hora % 12).toString().length < 2) "0${hora % 12}" else (hora % 12).toString()
        "$horaStr:$minStr pm"
    }
}

/**
 * Convierte tiempo de 24Hrs a 12 am o pm
 * @param hora "HH:mm:ss"
 */
fun formatHora(horaSrc: String): String {
    var hora = horaSrc.substring(0, 2).toInt()
    val minStr = horaSrc.substring(3, 5)
    return if (hora <= 12) {
        val horaStr = if (hora.toString().length < 2) "0$hora" else hora.toString()
        when (hora) {
            0 -> "12:$minStr am"
            12 -> "12:$minStr pm"
            else -> "$horaStr:$minStr am"
        }
    } else {
        val horaStr =
            if ((hora % 12).toString().length < 2) "0${hora % 12}" else (hora % 12).toString()
        "$horaStr:$minStr pm"
    }
}

fun formatHoraFin(horaIni: String, duracionMin: Int): String {
    var hora = horaIni.substring(0, 2).toInt()
    var min = horaIni.substring(3, 5).toInt()
    val horaAdd = duracionMin / 60
    val minAdd = duracionMin % 60
    min += minAdd
    hora += horaAdd + min / 60
    min %= 60
    return formatHora(hora, min)
}

fun obtenerHoraFin(horaIni: String, duracionMin: Int): String {
    var hora = horaIni.substring(0, 2).toInt()
    var min = horaIni.substring(3, 5).toInt()
    return minToTime(hora * 60 + min + duracionMin)
}

fun generarHorariosPrevio(
    horaIni: Int,
    minIni: Int,
    horaFin: Int,
    minFin: Int,
    intervalo: Int
): List<Horario> {
    val minutosIni = horaIni * 60 + minIni
    val minutosFin = horaFin * 60 + minFin
    val nuevosHorarios = mutableListOf<Horario>()
    //procede a generar horarios para ese dia
    var minutoHorarioInicio = minutosIni
    var minutoHorarioFin = minutoHorarioInicio + intervalo
    while (minutoHorarioFin <= minutosFin) {
        nuevosHorarios.add(
            Horario(
                id = 0, 0,
                fecha = "",
                horaIni = minToTime(minutoHorarioInicio),
                horaFin = minToTime(minutoHorarioFin),
            )
        )
        minutoHorarioInicio = minutoHorarioFin
        minutoHorarioFin += intervalo
    }
    return nuevosHorarios
}

fun minToTime(minutos: Int): String {
    var hora = (minutos / 60).toString()
    var min = (minutos % 60).toString()
    if (hora.length < 2) hora = "0$hora"
    if (min.length < 2) min = "0$min"
    return "$hora:$min:00"
}

fun formatearDistancia(meters: Int?): String {
    return if (meters != null) {
        if (meters < 1000)
            "$meters m"
        else if (meters < 10000) {
            val km: Float = meters / 1000f
            String.format("%.1f Km", km)
        } else if (meters < 500000) {
            val km: Int = meters / 1000
            String.format("%d Km", km)
        } else ""
    } else ""
}

fun calcularDistanciaEnMetros(
    latitude1: Double,
    longitude1: Double,
    latitude2: Double,
    longitude2: Double,
): Int {
    val theta = longitude1 - longitude2
    val distance = 60 * 1.1515 * (180 / PI) * acos(
        sin(latitude1 * (PI / 180)) * sin(latitude2 * (PI / 180)) +
                cos(latitude1 * (PI / 180)) * cos(latitude2 * (PI / 180)) * cos(
            theta * (PI / 180)
        )
    )
    //En metros
    return (distance * 1.609344 * 1000).roundToInt()
}

fun formatoFecha(milisUtc: Long): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = milisUtc
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(calendar.time)
}

fun formatoFechaDB(milisUtc: Long): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = milisUtc
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(calendar.time)
}

fun formatoFechaLimaDB(milisUtc: Long): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = milisUtc
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    format.timeZone = TimeZone.getTimeZone("America/Lima")
    return format.format(calendar.time)
}

fun formatoFechaHoraDB(milisUtc: Long): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = milisUtc
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    format.timeZone = TimeZone.getTimeZone("America/Lima")
    return format.format(calendar.time)
}

fun formatoFechaDBaHum(fechaDB: String): String {
    val year = fechaDB.substring(0, 4)
    val month = fechaDB.substring(5, 7)
    val day = fechaDB.substring(8, 10)
    return "$day/$month/$year"
}

fun formatoPrecio(precio: BigDecimal?): String = DecimalFormat("#,###.00").format(precio)