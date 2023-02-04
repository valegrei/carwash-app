package pe.com.carwashperuapp.carwashapp.ui.my_services

import android.icu.math.BigDecimal
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.servicio.Servicio
import pe.com.carwashperuapp.carwashapp.database.servicio.ServicioDao
import pe.com.carwashperuapp.carwashapp.database.sesion.Sesion
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import pe.com.carwashperuapp.carwashapp.network.request.ReqAddServicio
import pe.com.carwashperuapp.carwashapp.network.request.ReqModServicio
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR, NORMAL }
enum class EditStatus { NEW, EDIT, DELETE, NORMAL }
enum class GoStatus { GO_ADD, SHOW_DELETE, NORMAL }
class MyServicesViewModel(
    private val sesionData: SesionData,
    private val servicioDao: ServicioDao,
) : ViewModel() {
    private val TAG = MyServicesViewModel::class.simpleName

    //Datos para editar cuenta
    var precio = MutableLiveData<String>()
    var duracion = MutableLiveData<String>()
    var nombre = MutableLiveData<String>()

    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg
    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status
    private var _editStatus = MutableLiveData<EditStatus>()
    val editStatus: LiveData<EditStatus> = _editStatus
    private var _goStatus = MutableLiveData<GoStatus>()
    val goStatus: LiveData<GoStatus> = _goStatus

    private var _selectedService = MutableLiveData<Servicio>()
    val selectedService: LiveData<Servicio> = _selectedService

    fun setSelectedService(servicio: Servicio) {
        val selServ = servicio.copy()
        _selectedService.value = selServ
    }

    fun goNuevo() {
        nombre.value = ""
        precio.value = ""
        duracion.value = ""
        _errMsg.value = ""
        _editStatus.value = EditStatus.NEW
        _status.value = Status.NORMAL
        _goStatus.value = GoStatus.GO_ADD
    }

    fun goEditar() {
        _errMsg.value = ""
        val selServ = selectedService.value!!
        nombre.value = selServ.nombre
        precio.value = selServ.getPrecioFormateado()
        duracion.value = selServ.duracion.toString()
        _editStatus.value = EditStatus.EDIT
        _status.value = Status.NORMAL
        _goStatus.value = GoStatus.GO_ADD
    }

    fun goDelete() {
        _editStatus.value = EditStatus.DELETE
        _goStatus.value = GoStatus.SHOW_DELETE
    }

    fun cargarServicios(): Flow<List<Servicio>> {
        val sesion = sesionData.getCurrentSesion()
        return servicioDao.obtenerServicios(sesion?.usuario?.id!!)
    }

    fun descargarUsuarios() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroServicios()
            descargarServicios(sesion, lastSincro)
            _status.value = Status.SUCCESS
        }
    }

    fun clearEditStatus() {
        _editStatus.value = EditStatus.NORMAL
    }

    fun clearGoStatus() {
        _goStatus.value = GoStatus.NORMAL
    }

    fun guardar() {
        when (editStatus.value) {
            EditStatus.NEW -> agregarServicio()
            EditStatus.EDIT -> modificarServicio()
            else -> {}
        }
    }

    private fun agregarServicio() {
        val nombre = (this.nombre.value ?: "").trim()
        val precio = (this.precio.value ?: "").trim()
        val duracion = (this.duracion.value ?: "").trim()
        if (validar(nombre, precio, duracion)) {
            agregarServicio(nombre, precio, duracion)
        }
    }

    private fun modificarServicio() {
        val nombre = (this.nombre.value ?: "").trim()
        val precio = (this.precio.value ?: "").trim()
        val duracion = (this.duracion.value ?: "").trim()
        if (validar(nombre, precio, duracion)) {
            val modServicio = selectedService.value!!
            modServicio.nombre = nombre
            modServicio.precio = BigDecimal(precio)
            modServicio.duracion = duracion.toInt()
            modificarServicio(modServicio, false)
        }
    }

    fun eliminarServicio() {
        val modServicio = selectedService.value!!
        modServicio.estado = false//!modServicio.estado
        modificarServicio(modServicio, true)
    }

    private fun validar(nombre: String, precio: String, duracion: String): Boolean {
        _errMsg.value = ""
        if (nombre.isEmpty()) {
            _errMsg.value = "Campo Servicio vacío"
            return false
        }
        if (precio.isEmpty()) {
            _errMsg.value = "Campo Precio vacío"
            return false
        }
        if (duracion.isEmpty()) {
            _errMsg.value = "Campo Duración vacío"
            return false
        }
        return true
    }

    private fun agregarServicio(nombre: String, precio: String, duracion: String) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val reqAddServicio = ReqAddServicio(nombre, BigDecimal(precio), duracion.toInt())
            //guarda lo cambiado
            Api.retrofitService.agregarServicio(
                reqAddServicio, sesion?.getTokenBearer()!!
            )
            _editStatus.value = EditStatus.NORMAL
            _status.value = Status.SUCCESS
        }
    }

    private fun modificarServicio(modServicio: Servicio, descargar: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroServicios()
            val reqModServicio = ReqModServicio(
                modServicio.id,
                modServicio.nombre,
                modServicio.precio,
                modServicio.duracion,
                modServicio.estado
            )
            //guarda lo cambiado
            Api.retrofitService.modificarServicio(
                reqModServicio, sesion?.getTokenBearer()!!
            )
            //Trae los cambios
            if (descargar)
                descargarServicios(sesion, lastSincro)
            _editStatus.value = EditStatus.NORMAL
            _status.value = Status.SUCCESS
        }
    }


    private suspend fun descargarServicios(sesion: Sesion?, lastSincro: Date) {
        val res = Api.retrofitService.obtenerServicios(
            lastSincro,
            sesion?.getTokenBearer()!!
        )
        val servicios = res.data.servicios
        if (servicios.isNotEmpty()) {
            servicioDao.guardarServicios(servicios)
        }
        sesionData.saveLastSincroServicios(res.timeStamp)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        _errMsg.value = exceptionError.message
        _status.value = Status.ERROR
    }
}

class MyServicesViewModelFactory(
    private val sesionData: SesionData,
    private val servicioDao: ServicioDao,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyServicesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyServicesViewModel(sesionData, servicioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}