package pe.com.carwashperuapp.carwashapp.ui.my_schedules

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.direccion.Direccion
import pe.com.carwashperuapp.carwashapp.database.direccion.DireccionDao
import pe.com.carwashperuapp.carwashapp.database.horario.HorarioConfig
import pe.com.carwashperuapp.carwashapp.database.horario.HorarioConfigLocal
import pe.com.carwashperuapp.carwashapp.database.horario.HorarioDao
import pe.com.carwashperuapp.carwashapp.database.sesion.Sesion
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import pe.com.carwashperuapp.carwashapp.network.request.ReqHorarioConfig
import pe.com.carwashperuapp.carwashapp.ui.util.formatHora
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR }
enum class EditStatus { EXIT, EDIT, NEW, VIEW }
enum class GoStatus { GO_ADD, SHOW_DELETE, NORMAL }
class MySchedulesViewModel(
    private val sesionData: SesionData,
    private val horarioDao: HorarioDao,
    private val direccionDao: DireccionDao,
) : ViewModel() {
    private val TAG = MySchedulesViewModel::class.simpleName

    //errores
    private var _errMsg = MutableLiveData<String?>()
    val errMsg: LiveData<String?> = _errMsg

    //estados
    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status
    private var _editStatus = MutableLiveData<EditStatus>()
    val editStatus: LiveData<EditStatus> = _editStatus
    private var _goStatus = MutableLiveData<GoStatus>()
    val goStatus: LiveData<GoStatus> = _goStatus
    private var _mostrarEditar = MutableLiveData<Boolean>()
    val mostrarEditar: LiveData<Boolean> = _mostrarEditar

    //seleccionados
    private var _selectedHorarioConfig = MutableLiveData<HorarioConfig>()
    val selectedHorarioConfig: LiveData<HorarioConfig> = _selectedHorarioConfig

    //campos
    private var _freqTodosLosDias = MutableLiveData<Boolean>()
    val freqTodosLosDias: LiveData<Boolean> = _freqTodosLosDias
    private var _freqLunVie = MutableLiveData<Boolean>()
    val freqLunVie: LiveData<Boolean> = _freqLunVie
    private var _freqPersonalizado = MutableLiveData<Boolean>()
    val freqPersonalizado: LiveData<Boolean> = _freqPersonalizado
    private var _freqPersonalizadoText = MutableLiveData<String>()
    val freqPersonalizadoText: LiveData<String> = _freqPersonalizadoText
    private var _lunes = MutableLiveData<Boolean>()
    val lunes: LiveData<Boolean> = _lunes
    private var _martes = MutableLiveData<Boolean>()
    val martes: LiveData<Boolean> = _martes
    private var _miercoles = MutableLiveData<Boolean>()
    val miercoles: LiveData<Boolean> = _miercoles
    private var _jueves = MutableLiveData<Boolean>()
    val jueves: LiveData<Boolean> = _jueves
    private var _viernes = MutableLiveData<Boolean>()
    val viernes: LiveData<Boolean> = _viernes
    private var _sabado = MutableLiveData<Boolean>()
    val sabado: LiveData<Boolean> = _sabado
    private var _domingo = MutableLiveData<Boolean>()
    val domingo: LiveData<Boolean> = _domingo
    private var _horaIni = MutableLiveData<Int>()
    val horaIni: LiveData<Int> = _horaIni
    private var _minIni = MutableLiveData<Int>()
    val minIni: LiveData<Int> = _minIni
    private var _horarioIni = MutableLiveData<String>()
    val horarioIni: LiveData<String> = _horarioIni
    private var _horaFin = MutableLiveData<Int>()
    val horaFin: LiveData<Int> = _horaFin
    private var _minFin = MutableLiveData<Int>()
    val minFin: LiveData<Int> = _minFin
    private var _horarioFin = MutableLiveData<String>()
    val horarioFin: LiveData<String> = _horarioFin
    val nroAtenciones = MutableLiveData<String>()
    private var _locales = MutableLiveData<List<Direccion>>()
    val locales: LiveData<List<Direccion>> = _locales
    private var _local = MutableLiveData<Direccion>()
    val local: LiveData<Direccion> = _local

    fun nuevoHorarioConfig() {
        cargarDirecciones(null)
        _lunes.value = false
        _martes.value = false
        _miercoles.value = false
        _jueves.value = false
        _viernes.value = false
        _sabado.value = false
        _domingo.value = false
        _freqTodosLosDias.value = false
        _freqLunVie.value = false
        _freqPersonalizado.value = false
        _freqPersonalizadoText.value = "Personalizado"
        _horaIni.value = 0
        _minIni.value = 0
        _horarioIni.value = ""
        _horaFin.value = 0
        _minFin.value = 0
        _horarioFin.value = ""
        nroAtenciones.value = "1"
        _errMsg.value = ""
        _editStatus.value = EditStatus.NEW
        _mostrarEditar.value = true
    }

    fun verHorario(horarioConfig: HorarioConfig) {
        _selectedHorarioConfig.value = horarioConfig
        cargarDirecciones(horarioConfig)
        _errMsg.value = ""
        _editStatus.value = EditStatus.VIEW
        _mostrarEditar.value = false
    }

    fun editarHorario() {
        val horarioConfig = _selectedHorarioConfig.value!!
        _lunes.value = horarioConfig.lunes
        _martes.value = horarioConfig.martes
        _miercoles.value = horarioConfig.miercoles
        _jueves.value = horarioConfig.jueves
        _viernes.value = horarioConfig.viernes
        _sabado.value = horarioConfig.sabado
        _domingo.value = horarioConfig.domingo
        setFreqs()
        setHorarioIni(horarioConfig.horaIni, horarioConfig.minIni)
        setHorarioFin(horarioConfig.horaFin, horarioConfig.minFin)
        nroAtenciones.value = horarioConfig.nroAtenciones.toString()
        _errMsg.value = ""
        _editStatus.value = EditStatus.EDIT
        _mostrarEditar.value = true
    }

    private fun setFreqs() {
        if (lunes.value!! && martes.value!! && miercoles.value!! && jueves.value!!
            && viernes.value!! && sabado.value!! && domingo.value!!
        ) {
            _freqTodosLosDias.value = true
            _freqLunVie.value = false
            _freqPersonalizado.value = false
            _freqPersonalizadoText.value = "Personalizado"
        } else if (lunes.value!! && martes.value!! && miercoles.value!! && jueves.value!!
            && viernes.value!! && !sabado.value!! && !domingo.value!!
        ) {
            _freqTodosLosDias.value = false
            _freqLunVie.value = true
            _freqPersonalizado.value = false
            _freqPersonalizadoText.value = "Personalizado"
        } else {
            var text = "Personalizado ("
            if (lunes.value!!) text += " Lun"
            if (martes.value!!) text += " Mar"
            if (miercoles.value!!) text += " Mie"
            if (jueves.value!!) text += " Jue"
            if (viernes.value!!) text += " Vie"
            if (sabado.value!!) text += " Sab"
            if (domingo.value!!) text += " Dom"
            text += " )"
            _freqTodosLosDias.value = false
            _freqLunVie.value = false
            _freqPersonalizado.value = true
            _freqPersonalizadoText.value = text
        }
    }

    private fun setDias(
        lunes: Boolean, martes: Boolean, miercoles: Boolean, jueves: Boolean,
        viernes: Boolean, sabado: Boolean, domingo: Boolean
    ) {
        _lunes.value = lunes
        _martes.value = martes
        _miercoles.value = miercoles
        _jueves.value = jueves
        _viernes.value = viernes
        _sabado.value = sabado
        _domingo.value = domingo
        setFreqs()
    }

    fun setTodosLosDias() {
        setDias(
            lunes = true,
            martes = true,
            miercoles = true,
            jueves = true,
            viernes = true,
            sabado = true,
            domingo = true,
        )
    }

    fun setLunVie() {
        setDias(
            lunes = true,
            martes = true,
            miercoles = true,
            jueves = true,
            viernes = true,
            sabado = false,
            domingo = false,
        )
    }

    fun setPersonalizado(dias: BooleanArray) {
        setDias(
            lunes = dias[0],
            martes = dias[1],
            miercoles = dias[2],
            jueves = dias[3],
            viernes = dias[4],
            sabado = dias[5],
            domingo = dias[6],
        )
    }

    fun setHorarioIni(hora: Int, min: Int) {
        _horaIni.value = hora
        _minIni.value = min
        _horarioIni.value = formatHora(hora, min)
    }

    fun setHorarioFin(hora: Int, min: Int) {
        _horaFin.value = hora
        _minFin.value = min
        _horarioFin.value = formatHora(hora, min)
    }

    fun cargarHorarioConfigs(): Flow<List<HorarioConfigLocal>> {
        val sesion = sesionData.getCurrentSesion()
        return horarioDao.obtenerHorarioConfigLocales(sesion?.usuario?.id!!)
    }

    fun descargarHorarioConfigs() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroHorarioConfigs()
            try {
                descargarDirecciones(sesion, lastSincro)
            } catch (_: Exception) {
            }
            descargarHorarioConfigs(sesion, lastSincro)
            _status.value = Status.SUCCESS
        }
    }

    private suspend fun descargarHorarioConfigs(sesion: Sesion?, lastSincro: Date) {
        val res = Api.retrofitService.obtenerHorarioConfigs(
            lastSincro,
            sesion?.getTokenBearer()!!
        )
        val horarioConfigs = res.data.horarioConfigs
        if (horarioConfigs.isNotEmpty()) {
            horarioDao.guardarHorarioConfig(horarioConfigs)
        }
        sesionData.saveLastSincroHorarioConfigs(res.timeStamp)
    }

    private fun crearHorarioConfig() {
        if (validar(null)) {
            crearHorarioConfig2()
        }
    }

    private fun crearHorarioConfig2() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()

            val reqHorarioConfig = ReqHorarioConfig(
                lunes = lunes.value!!,
                martes = martes.value!!,
                miercoles = miercoles.value!!,
                jueves = jueves.value!!,
                viernes = viernes.value!!,
                sabado = sabado.value!!,
                domingo = domingo.value!!,
                horaIni = horaIni.value!!,
                minIni = minIni.value!!,
                horaFin = horaFin.value!!,
                minFin = minFin.value!!,
                nroAtenciones = (nroAtenciones.value ?: "0").toInt(),
                idLocal = local.value?.id ?: 0,
            )

            Api.retrofitService.agregarHorarioConfig(
                reqHorarioConfig,
                sesion?.getTokenBearer()!!
            )

            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }

    private fun validar(idHorarioConfig: Int?): Boolean {
        _errMsg.value = null
        if ((local.value?.id ?: 0) == 0) {
            _errMsg.value = "Debe seleccionar un local"
            return false
        }
        if (!lunes.value!! && !martes.value!! && !miercoles.value!! && !jueves.value!!
            && !viernes.value!! && !sabado.value!! && !domingo.value!!
        ) {
            _errMsg.value = "Debe seleccionar una frecuencia de días"
            return false
        }
        if ((horaIni.value ?: 0) >= (horaFin.value ?: 0)) {
            _errMsg.value = "Seleccione horas de inicio y fin correctos"
            return false
        }
        val nroAtenc = (nroAtenciones.value ?: "0").toInt()
        if ( nroAtenc <= 0 || nroAtenc > 10) {
            _errMsg.value = "Ingrese una cantidad correcta"
            return false
        }
        val cant = horarioDao.verificarConflictos(
            local.value?.id ?: 0, idHorarioConfig ?: 0
        )
        if (cant > 0) {
            _errMsg.value = "Ya se registró un horario para este local"
            return false
        }
        return true
    }

    fun eliminarHorarioConfig() {
        val idHorarioConfig = selectedHorarioConfig.value?.id!!
        eliminarHorarioConfig(idHorarioConfig)
    }

    private fun eliminarHorarioConfig(idHorarioConfig: Int) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()

            Api.retrofitService.eliminarHorarioConfig(
                idHorarioConfig,
                sesion?.getTokenBearer()!!
            )
            horarioDao.eliminarHorarioConfig(selectedHorarioConfig.value!!)
            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }

    private fun actualizarHorarioConfig() {
        val idHorarioConfig = selectedHorarioConfig.value?.id!!
        if (validar(idHorarioConfig)) {
            actualizarHorarioConfig(idHorarioConfig)
        }
    }

    private fun actualizarHorarioConfig(idHorarioConfig: Int) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()

            val reqHorarioConfig = ReqHorarioConfig(
                lunes = lunes.value!!,
                martes = martes.value!!,
                miercoles = miercoles.value!!,
                jueves = jueves.value!!,
                viernes = viernes.value!!,
                sabado = sabado.value!!,
                domingo = domingo.value!!,
                horaIni = horaIni.value!!,
                minIni = minIni.value!!,
                horaFin = horaFin.value!!,
                minFin = minFin.value!!,
                nroAtenciones = (nroAtenciones.value ?: "0").toInt(),
                idLocal = local.value?.id ?: 0,
            )

            Api.retrofitService.modificarHorarioConfig(
                idHorarioConfig,
                reqHorarioConfig,
                sesion?.getTokenBearer()!!
            )

            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }

    fun guardarHorarioConfig() {
        when (editStatus.value) {
            EditStatus.EDIT -> actualizarHorarioConfig()
            EditStatus.NEW -> crearHorarioConfig()
            else -> {}
        }
    }

    fun cargarDirecciones(horarioConfig: HorarioConfig?) {
        viewModelScope.launch {
            val sesion = sesionData.getCurrentSesion()
            val locales = direccionDao.obtenerDirecciones2(sesion?.usuario?.id!!)
            var local = Direccion(
                0,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                false,
                0,
                0
            )
            if (horarioConfig != null) {
                local = locales.find { it.id == horarioConfig.idLocal } ?: local
            }
            _local.value = local
            _locales.value = locales
        }
    }

    fun setSelectedLocal(local: Direccion) {
        _local.value = local
    }

    private suspend fun descargarDirecciones(sesion: Sesion?, lastSincro: Date) {
        val res = Api.retrofitService.obtenerDirecciones(
            lastSincro,
            sesion?.getTokenBearer()!!
        )
        val direcciones = res.data.direcciones
        if (direcciones.isNotEmpty()) {
            direccionDao.guardarDirecciones(direcciones)
        }
        sesionData.saveLastSincroDirecciones(res.timeStamp)
    }

    /*private fun buildQuery(
        lunes: Boolean, martes: Boolean, miercoles: Boolean,
        jueves: Boolean, viernes: Boolean, sabado: Boolean, domingo: Boolean,
        horaIni: Int, horaFin: Int, idLocal: Int, idHorarioConfig: Int?
    ): SimpleSQLiteQuery {
        var query = "SELECT COUNT(*) FROM horario_config WHERE "
        if (idHorarioConfig != null) query += "id != $idHorarioConfig AND "
        query += "idLocal = $idLocal AND estado = 1 AND ("
        if (lunes) query += "(lunes = 1) OR "
        if (martes) query += "(martes = 1) OR "
        if (miercoles) query += "(miercoles = 1) OR "
        if (jueves) query += "(jueves = 1) OR "
        if (viernes) query += "(viernes = 1) OR "
        if (sabado) query += "(sabado = 1) OR "
        if (domingo) query += "(domingo = 1) OR "
        query += "1=0) AND "
        query += "( MAX(horaIni, $horaIni) - MIN(horaFin, $horaFin) ) <= 0 "
        return SimpleSQLiteQuery(query)
    }*/

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        _errMsg.value = exceptionError.message
        _status.value = Status.ERROR
    }
}

class MySchedulesViewModelFactory(
    private val sesionData: SesionData,
    private val horarioDao: HorarioDao,
    private val direccionDao: DireccionDao,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MySchedulesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MySchedulesViewModel(sesionData, horarioDao, direccionDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}