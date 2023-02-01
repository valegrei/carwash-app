package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.icu.math.BigDecimal
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.direccion.DireccionDao
import pe.com.carwashperuapp.carwashapp.database.vehiculo.Vehiculo
import pe.com.carwashperuapp.carwashapp.database.vehiculo.VehiculoDao
import pe.com.carwashperuapp.carwashapp.model.Horario
import pe.com.carwashperuapp.carwashapp.model.Local
import pe.com.carwashperuapp.carwashapp.model.ServicioReserva
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import pe.com.carwashperuapp.carwashapp.network.request.ReqReservar
import pe.com.carwashperuapp.carwashapp.ui.util.calcularDistanciaEnMetros
import pe.com.carwashperuapp.carwashapp.ui.util.formatearDistancia
import pe.com.carwashperuapp.carwashapp.ui.util.formatoFechaDB
import pe.com.carwashperuapp.carwashapp.ui.util.formatoFechaHoraDB
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR }
enum class EditStatus { EXIT, EDIT, NEW, VIEW }
enum class GoStatus { GO_ADD, GO_CONFIRM, GO_LIST, NORMAL }
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

    fun nuevaReserva() {
        loadVehiculos()
        _errMsg.value = ""
        _servicios.value = selectedLocal.value?.distrib?.servicios!!
        _selectedFecha.value = MaterialDatePicker.todayInUtcMilliseconds()
        _horarios.value = listOf()
        _horariosMap.value = mapOf()
        buscarHorarios()
        _editStatus.value = EditStatus.NEW
        _goStatus.value = GoStatus.GO_ADD
        _mostrarEditar.value = true
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

    private fun loadVehiculos() {
        viewModelScope.launch {
            val sesion = sesionData.getCurrentSesion()!!
            var vehiculos = vehiculoDao.obtenerVehiculos2(sesion.usuario.id!!)
            if (vehiculos.isEmpty()) {
                vehiculos = listOf(
                    Vehiculo(
                        id = 0,
                        marca = "No hay vehículos registrados.",
                        modelo = "",
                        year = 0,
                        placa = "",
                        idCliente = 0,
                        estado = false,
                        path = ""
                    )
                )
            }
            _vehiculos.value = vehiculos
            _selectedVehiculo.value = this@ReserveViewModel.vehiculos.value?.get(0)
        }
    }

    fun seleccionarVehiculo(vehiculo: Vehiculo) {
        _selectedVehiculo.value = vehiculo
    }

    fun seleccionarFecha(milisUTC: Long) {
        _selectedFecha.value = milisUTC
        buscarHorarios()
    }

    fun seleccionarHorario(horario: Horario?) {
        _selectedHorario.value = horario
    }

    private fun buscarHorarios() {
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
                _horarios.value = resp.data.horarios
            } catch (_: Exception) {
                _horarios.value = listOf()
            }
            _status.value = Status.SUCCESS
        }
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
        }
        if (selectedServicios.value?.isEmpty()!!) {
            _errMsg.value = "Debe seleccionar servicios"
            return false
        }
        return true
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
            val sesion = sesionData.getCurrentSesion()
            val idHorario = selectedHorario.value?.id!!
            val idCliente = sesion?.usuario?.id!!
            val idVehiculo = selectedVehiculo.value?.id!!
            val servicios = selectedServicios.value!!
            val req = ReqReservar(idHorario, idCliente, idVehiculo, servicios)
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