package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.icu.math.BigDecimal
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.direccion.Direccion
import pe.com.carwashperuapp.carwashapp.database.direccion.DireccionDao
import pe.com.carwashperuapp.carwashapp.database.vehiculo.Vehiculo
import pe.com.carwashperuapp.carwashapp.database.vehiculo.VehiculoDao
import pe.com.carwashperuapp.carwashapp.model.*
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import pe.com.carwashperuapp.carwashapp.network.request.ReqFavorito
import pe.com.carwashperuapp.carwashapp.network.request.ReqReservar
import pe.com.carwashperuapp.carwashapp.ui.util.*
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR }
enum class EditStatus { EXIT, EDIT, NEW, VIEW }
enum class GoStatus { GO_ADD, GO_CONFIRM, GO_LIST, SHOW_COMPLETAR, SHOW_VEHICULOS, NORMAL }
class ReserveViewModel(
    private val sesionData: SesionData,
    private val direccionDao: DireccionDao,
    private val vehiculoDao: VehiculoDao,
) : ViewModel() {
    private val TAG = ReserveViewModel::class.simpleName

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
    private var _locales = MutableLiveData<List<Local>>()
    val locales: LiveData<List<Local>> = _locales
    private var _markersData = MutableLiveData<Map<Marker, Local>>()
    val markersData: LiveData<Map<Marker, Local>> = _markersData
    private var _selectedLocal = MutableLiveData<Local>()
    val selectedLocal: LiveData<Local> = _selectedLocal
    private var _distanceToLocal = MutableLiveData<String>()
    val distanceToLocal: LiveData<String> = _distanceToLocal
    private var _vehiculos = MutableLiveData<List<Vehiculo>>()
    val vehiculos: LiveData<List<Vehiculo>> = _vehiculos
    private var _selectedVehiculo = MutableLiveData<Vehiculo>()
    val selectedVehiculo: LiveData<Vehiculo> = _selectedVehiculo
    private var _servicios = MutableLiveData<List<ServicioReserva>>()
    val servicios: LiveData<List<ServicioReserva>> = _servicios
    private var _selectedServicios = MutableLiveData<List<ServicioReserva>>()
    val selectedServicios: LiveData<List<ServicioReserva>> = _selectedServicios
    private var _selectedFecha = MutableLiveData<Long>()
    val selectedFecha: LiveData<Long> = _selectedFecha
    private var _horarios = MutableLiveData<List<Horario>>()
    val horarios: LiveData<List<Horario>> = _horarios
    private var _horariosMap = MutableLiveData<Map<Int, Horario>>()
    val horariosMap: LiveData<Map<Int, Horario>> = _horariosMap
    private var _selectedHorario = MutableLiveData<Horario?>()
    val selectedHorario: LiveData<Horario?> = _selectedHorario
    private var _totalServicios = MutableLiveData<BigDecimal>()
    val totalServicios: LiveData<BigDecimal> = _totalServicios
    private var _totalDuracion = MutableLiveData<Int>()
    val totalDuracion: LiveData<Int> = _totalDuracion
    private var _favorito = MutableLiveData<Favorito?>()
    val favorito: LiveData<Favorito?> = _favorito
    private var _mostrarFavorito = MutableLiveData<Boolean>()
    val mostrarFavorito: LiveData<Boolean> = _mostrarFavorito
    private var _selectedLatLng = MutableLiveData<LatLng?>()
    val selectedLatLng: LiveData<LatLng?> = _selectedLatLng
    private var _selectedHorarioLocal = MutableLiveData<HorarioLocal?>()
    val selectedHorarioLocal: LiveData<HorarioLocal?> = _selectedHorarioLocal
    private var _selectedHorarioHelper = MutableLiveData<HorarioHelper>()
    val selectedHorarioHelper: LiveData<HorarioHelper> = _selectedHorarioHelper

    fun goLocal() {
        nuevaReserva()
    }


    fun completarOReservar() {
        if (datosCompletos()) {
            reservar()
        } else {
            mostrarCompletarDatos()
        }
    }

    private fun mostrarCompletarDatos() {
        _goStatus.value = GoStatus.SHOW_COMPLETAR
    }

    fun mostrarAgregarVehiculos() {
        _goStatus.value = GoStatus.SHOW_VEHICULOS
    }

    fun clearErr() {
        _errMsg.value = ""
    }

    private fun nuevaReserva() {
        cargarFavorito()
        clearErr()
        _servicios.value = selectedLocal.value?.distrib?.servicios!!
        _selectedFecha.value = MaterialDatePicker.todayInUtcMilliseconds()
        seleccionarHorarioLocal()
        _selectedLatLng.value = LatLng(
            selectedLocal.value?.latitud!!.toDouble(),
            selectedLocal.value?.longitud!!.toDouble()
        )
        _horarios.value = listOf()
        _horariosMap.value = mapOf()
        buscarHorarios()
        _editStatus.value = EditStatus.NEW
        _goStatus.value = GoStatus.GO_ADD
        _mostrarEditar.value = true
    }

    private fun seleccionarHorarioLocal() {
        //Por dia de semana
        val fecha = _selectedFecha.value!!
        val calendar = Calendar.getInstance()
        val horarioLocalList = _selectedLocal.value?.horarios!!
        calendar.timeInMillis = fecha
        var selHorLoc: HorarioLocal? = null
        horarioLocalList.forEach {
            val esElDia = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                1 -> it.lunes
                2 -> it.martes
                3 -> it.miercoles
                4 -> it.jueves
                5 -> it.viernes
                6 -> it.sabado
                7 -> it.domingo
                else -> false
            }
            if (esElDia) {
                selHorLoc = it
                return@forEach
            }
        }
        _selectedHorarioLocal.value = selHorLoc
    }

    private fun cargarFavorito() {
        val favs = selectedLocal.value?.favoritos
        if (favs.isNullOrEmpty()) {
            _favorito.value = null
            _mostrarFavorito.value = false
        } else {
            _favorito.value = favs[0]
            _mostrarFavorito.value = true
        }
    }

    fun marcarFavorito() {
        _mostrarFavorito.value = true
    }

    fun desmarcarFavorito() {
        _mostrarFavorito.value = false
    }

    fun mostrarLlamar(): Boolean {
        return (selectedLocal.value?.distrib?.nroCel1 ?: "").isNotEmpty()
    }

    fun mostrarWhatsapp(): Boolean{
        return (selectedLocal.value?.distrib?.nroCel2 ?: "").isNotEmpty()
    }

    fun guardarFavorito() {
        if (mostrarFavorito.value!!) {
            // revisar si antes no estaba marcado
            if (favorito.value == null) agregarFavorito()
        } else {
            // revisar si antes estaba marcado
            if (favorito.value != null) eliminarFavorito()
        }
    }

    private fun eliminarFavorito() {
        viewModelScope.launch(exceptionHandler) {
            val sesion = sesionData.getCurrentSesion()
            try {
                Api.retrofitService.eliminarFavorito(
                    favorito.value?.id!!,
                    sesion?.getTokenBearer()!!,
                )
                _favorito.value = null
                _mostrarFavorito.value = false
            } catch (_: Exception) {
                _mostrarFavorito.value = true
            }
        }
    }

    private fun agregarFavorito() {
        viewModelScope.launch(exceptionHandler) {
            val sesion = sesionData.getCurrentSesion()
            try {
                val res = Api.retrofitService.agregarFavorito(
                    ReqFavorito(selectedLocal.value?.id!!),
                    sesion?.getTokenBearer()!!,
                )
                _favorito.value = res.data.favorito
                _mostrarFavorito.value = true
            } catch (e: Exception) {
                _favorito.value = null
                _mostrarFavorito.value = false
            }
        }
    }

    fun consultarLocales(cornerNE: LatLng, cornerSW: LatLng) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            try {
                val res = Api.retrofitService.obtenerLocales(
                    cornerNE.latitude.toString(),
                    cornerNE.longitude.toString(),
                    cornerSW.latitude.toString(),
                    cornerSW.longitude.toString(),
                    sesion?.getTokenBearer()!!,
                )
                _locales.value = res.data.locales
            } catch (e: Exception) {
                _locales.value = listOf()
            }
            _status.value = Status.SUCCESS
        }
    }

    fun obtenerFavoritos() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            try {
                val res = Api.retrofitService.obtenerLocalesFavoritos(
                    sesion?.getTokenBearer()!!,
                )
                _locales.value = res.data.locales
            } catch (e: Exception) {
                _locales.value = listOf()
            }
            _status.value = Status.SUCCESS
        }
    }

    fun setMarkersData(map: Map<Marker, Local>) {
        _markersData.value = map
    }

    fun selectLocal(marker: Marker, currentLocation: LatLng) {
        val local = markersData.value!![marker]!!
        _selectedLocal.value = local
        _distanceToLocal.value = formatearDistancia(
            calcularDistanciaEnMetros(
                marker.position.latitude, marker.position.longitude,
                currentLocation.latitude, currentLocation.longitude
            )
        )
    }

    fun selectLocal(local: Local) {
        _selectedLocal.value = local
    }

    fun obtenerDirecciones(): Flow<List<Direccion>> {
        val sesion = sesionData.getCurrentSesion()!!
        return direccionDao.obtenerDirecciones(sesion.usuario.id!!)
    }

    fun loadVehiculos(): Flow<List<Vehiculo>> {
        val sesion = sesionData.getCurrentSesion()!!
        return vehiculoDao.obtenerVehiculos(sesion.usuario.id!!)
    }

    fun loadVehiculos2(vehiculos: List<Vehiculo>) {
        val lista = mutableListOf<Vehiculo>()
        if (vehiculos.isEmpty()) {
            lista += listOf(
                Vehiculo(
                    id = 0,
                    marca = "",
                    modelo = "",
                    year = 0,
                    placa = "",
                    idCliente = 0,
                    estado = false,
                    path = ""
                ),
                Vehiculo(
                    id = -1,
                    marca = "",
                    modelo = "",
                    year = 0,
                    placa = "",
                    idCliente = 0,
                    estado = false,
                    path = ""
                )
            )
        } else {
            lista += vehiculos + listOf(
                Vehiculo(
                    id = -1,
                    marca = "",
                    modelo = "",
                    year = 0,
                    placa = "",
                    idCliente = 0,
                    estado = false,
                    path = ""
                )
            )
        }
        _vehiculos.value = lista
        _selectedVehiculo.value = this@ReserveViewModel.vehiculos.value?.get(0)
    }

    fun seleccionarVehiculo(vehiculo: Vehiculo) {
        _selectedVehiculo.value = vehiculo
    }

    fun seleccionarFecha(milisUTC: Long) {
        _selectedFecha.value = milisUTC
        seleccionarHorarioLocal()
        buscarHorarios()
    }

    fun seleccionarHorario(horario: Horario?) {
        _selectedHorario.value = horario
    }

    fun clearLocales() {
        _locales.value = listOf()
    }

    private fun buscarHorarios() {
        val nroAtenciones = selectedHorarioLocal.value?.nroAtenciones ?: 0
        if (nroAtenciones > 0) {
            buscarHorarios2()
        } else {
            setHorarios(null)
        }
    }

    private fun buscarHorarios2() {
        viewModelScope.launch {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val fecha = formatoFechaDB(selectedFecha.value!!)
            val fechaHora = formatoFechaHoraDB(Date().time)
            val idLocal = selectedLocal.value?.id!!
            try {
                val resp = Api.retrofitService.obtenerHorarios(
                    idLocal, fecha, fechaHora, sesion?.getTokenBearer()!!
                )
                setHorarios(resp.data.horarios)
            } catch (_: Exception) {
                setHorarios(listOf())
            }
            _status.value = Status.SUCCESS
        }
    }

    private fun setHorarios(horarios: List<Horario>?) {
        val horarioHelper = HorarioHelper(selectedHorarioLocal.value, horarios)
        _horarios.value = horarioHelper.obtenerListaHorarios()
        _selectedHorarioHelper.value = horarioHelper
    }

    fun setHorarioMap(map: Map<Int, Horario>) {
        _horariosMap.value = map
    }

    private fun clearErrs() {
        _errMsg.value = null
    }

    fun clearGoStatus() {
        _goStatus.value = GoStatus.NORMAL
    }

    fun validar(): Boolean {
        clearErrs()
        if ((selectedVehiculo.value?.id ?: 0) == 0) {
            _errMsg.value = "Debe seleccionar un vehículo"
            return false
        }
        if (selectedHorario.value == null) {
            _errMsg.value = "Debe seleccionar un horario"
            return false
        } else if (!validarRango()) {
            _errMsg.value = "No se puede reservar debido a tiempo insuficiente"
            return false
        }
        if (selectedServicios.value?.isEmpty()!!) {
            _errMsg.value = "Debe seleccionar servicios"
            return false
        }
        return true
    }

    private fun validarRango(): Boolean {
        calcularDuracionServicios()
        val horarioHelper = selectedHorarioHelper.value
        val selHorario = selectedHorario.value!!
        val duracion = totalDuracion.value!!
        val rangoHorarios = horarioHelper?.obtenerRangoHorarioValido(selHorario,duracion)
        return !rangoHorarios.isNullOrEmpty()
    }


    private fun calcularDuracionServicios() {
        var total = 0
        selectedServicios.value?.forEach {
            total += it.duracion!!
        }
        _totalDuracion.value = total
    }

    fun reservar() {
        if (validar()) {
            calcularTotalServicios()
            _goStatus.value = GoStatus.GO_CONFIRM
        }
    }

    private fun calcularTotalServicios() {
        var total = BigDecimal.ZERO
        selectedServicios.value?.forEach {
            total = total.add(it.precio)
        }
        _totalServicios.value = total
    }

    fun confirmarReserva() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val horarioHelper = selectedHorarioHelper.value
            val horarioIni = selectedHorario.value!!
            val duracionTotal = totalDuracion.value!!
            val rangoHorarios = horarioHelper?.obtenerRangoHorarioValido(horarioIni,duracionTotal)!!
            val idHorarios = rangoHorarios.map { it.id }
            val sesion = sesionData.getCurrentSesion()
            val idCliente = sesion?.usuario?.id!!
            val idVehiculo = selectedVehiculo.value?.id!!
            val idDistrib = selectedLocal.value?.distrib?.id!!
            val idLocal = selectedLocal.value?.id!!
            val servicios = selectedServicios.value!!
            val req = ReqReservar(
                idHorarios,
                idCliente,
                idVehiculo,
                idDistrib,
                idLocal,
                horarioIni.fecha,
                horarioIni.horaIni,
                horarioIni.fechaHoraDB(),
                duracionTotal,
                servicios
            )
            Api.retrofitService.crearReserva(req, sesion.getTokenBearer())
            _status.value = Status.SUCCESS
            _goStatus.value = GoStatus.GO_LIST
        }
    }

    fun seleccionadosServicios() {
        val seleccionados = servicios.value?.filter {
            it.seleccionado!!
        }
        _selectedServicios.value = seleccionados!!
    }

    fun datosCompletos(): Boolean {
        val sesion = sesionData.getCurrentSesion()
        val usu = sesion?.usuario
        return !usu?.nombres.isNullOrEmpty()
                && !usu?.apellidoPaterno.isNullOrEmpty()
                && !usu?.nroDocumento.isNullOrEmpty()
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        _errMsg.value = exceptionError.message
        _status.value = Status.ERROR
    }
}

class ReserveViewModelFactory(
    private val sesionData: SesionData,
    private val direccionDao: DireccionDao,
    private val vehiculoDao: VehiculoDao,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReserveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReserveViewModel(sesionData, direccionDao, vehiculoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}