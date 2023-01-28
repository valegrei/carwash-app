package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.direccion.DireccionDao
import pe.com.carwashperuapp.carwashapp.database.vehiculo.Vehiculo
import pe.com.carwashperuapp.carwashapp.database.vehiculo.VehiculoDao
import pe.com.carwashperuapp.carwashapp.model.Local
import pe.com.carwashperuapp.carwashapp.model.ServicioReserva
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import pe.com.carwashperuapp.carwashapp.ui.util.calcularDistanciaEnMetros
import pe.com.carwashperuapp.carwashapp.ui.util.formatearDistancia
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR }
enum class EditStatus { EXIT, EDIT, NEW, VIEW }
enum class GoStatus { GO_ADD, SHOW_DELETE, NORMAL }
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
    private var _selectedFecha = MutableLiveData<Date>()
    val selectedFecha: LiveData<Date> = _selectedFecha

    fun nuevaReserva() {
        _errMsg.value = ""
        _servicios.value = selectedLocal.value?.distrib?.servicios!!
        _selectedFecha.value = Date()
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
            _vehiculos.value = vehiculos
        }
    }

    fun seleccionarVehiculo(vehiculo: Vehiculo) {
        _selectedVehiculo.value = vehiculo
    }

    fun seleccionarFecha(date: Date){
        _selectedFecha.value = date
    }
    init {
        loadVehiculos()
    }

    private fun clearErrs() {
        _errMsg.value = null
    }

    fun clearGoStatus() {
        _goStatus.value = GoStatus.NORMAL
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