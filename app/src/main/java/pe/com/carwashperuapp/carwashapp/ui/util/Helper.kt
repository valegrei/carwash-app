package pe.com.carwashperuapp.carwashapp.ui.util

import pe.com.carwashperuapp.carwashapp.model.Horario

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

fun generarHorariosPrevio(horaIni: Int, minIni: Int, horaFin: Int, minFin: Int, intervalo: Int): List<Horario>{
    val minutosIni = horaIni * 60 + minIni
    val minutosFin = horaFin * 60 + minFin
    val nuevosHorarios = mutableListOf<Horario>()
    //procede a generar horarios para ese dia
    var minutoHorarioInicio = minutosIni
    var minutoHorarioFin = minutoHorarioInicio + intervalo
    while (minutoHorarioFin <= minutosFin) {
        nuevosHorarios.add(
            Horario(
                id = 0,
                fecha = "",
                horaIni = minToTime(minutoHorarioInicio),
                horaFin = minToTime(minutoHorarioFin),
                estado = true,
                idDistrib = 0,
                idLocal = 0,
                idHorarioConfig = 0,
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