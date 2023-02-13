package pe.com.carwashperuapp.carwashapp.ui.reserve

import pe.com.carwashperuapp.carwashapp.model.Horario
import pe.com.carwashperuapp.carwashapp.model.HorarioLocal
import pe.com.carwashperuapp.carwashapp.ui.util.obtenerHoraFin

class HorarioHelper(
    private val _horarioConfig: HorarioLocal?,
    private val _horariosRaw: List<Horario>?,
) {
    private lateinit var _listaH: Array<MutableList<Horario>>
    private lateinit var _listaV: Map<Horario, MutableList<Horario>>

    init {
        crearListaH()
        crearListaV()
    }

    private fun crearListaH() {
        _listaH = if (_horarioConfig == null || _horariosRaw.isNullOrEmpty()) {
            arrayOf()
        } else {
            val arr = Array<MutableList<Horario>>(_horarioConfig.nroAtenciones) { mutableListOf() }
            for (h in _horariosRaw) {
                arr[h.nro].add(h)
            }
            arr
        }
    }

    private fun crearListaV() {
        _listaV = if (_horarioConfig == null || _horariosRaw.isNullOrEmpty()) {
            mutableMapOf()
        } else {
            val map = mutableMapOf<Horario, MutableList<Horario>>()
            var horaIniTmp = ""
            var horarioTmp: Horario? = null
            for (h in _horariosRaw) {
                if (h.horaIni != horaIniTmp) {
                    //Si es otro horaIni, asigna nuevo key y nueva lista
                    map[h] = mutableListOf(h)
                    horaIniTmp = h.horaIni
                    horarioTmp = h
                } else {
                    //Sino, adiere otro horario a la lista
                    map[horarioTmp!!]?.add(h)
                }
            }
            map
        }
    }

    //Obtiene lista para mostrar
    fun obtenerListaHorarios(): List<Horario> {
        return if (_listaV.isEmpty()) {
            listOf()
        } else {
            _listaV.keys.toList()
        }
    }

    fun obtenerRangoHorarioValido(selectedHorario: Horario, duracionTotal: Int): List<Horario>? {
        //recorremos la lista en el mapa
        for (hIni in _listaV[selectedHorario]!!) {
            //buscamos en la lista horarios validos segun nro de atencion
            val horarios = obtenerRangoHorarioH(hIni, duracionTotal)
            if (horarios != null)
                return horarios
        }
        return null
    }

    private fun obtenerRangoHorarioH(hIni: Horario, duracionTotal: Int): List<Horario>? {
        val horaFin = obtenerHoraFin(hIni.horaIni, duracionTotal)
        val indIni = _listaH[hIni.nro].indexOf(hIni)
        val lista = mutableListOf<Horario>()
        lista.add(hIni)
        if (hIni.horaFin >= horaFin) {
            return lista    //cumple duracion
        }
        for (i in indIni + 1 until _listaH[hIni.nro].size) {
            val horario = _listaH[hIni.nro][i]
            val horarioAnt = lista[lista.size - 1]
            //Comprabando continuidad
            if (horarioAnt.horaFin != horario.horaIni) {
                return null //no es continuo
            }
            lista.add(horario)
            if (horario.horaFin >= horaFin) {
                return lista    //cumple duracion
            }
        }
        return null
    }
}