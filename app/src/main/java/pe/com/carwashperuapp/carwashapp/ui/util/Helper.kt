package pe.com.carwashperuapp.carwashapp.ui.util

fun formatHora(hora: Int, min: Int): String {
    val minStr = if (min.toString().length < 2) "0$min" else min.toString()
    return if (hora <= 12) {
        val horaStr = if (hora.toString().length < 2) "0$hora" else hora.toString()
        when (hora) {
            0 -> "12:$minStr AM"
            12 -> "12:$minStr PM"
            else -> "$horaStr:$minStr AM"
        }
    } else {
        val horaStr =
            if ((hora % 12).toString().length < 2) "0${hora % 12}" else (hora % 12).toString()
        "$horaStr:$minStr PM"
    }
}