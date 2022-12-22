package pe.com.valegrei.carwashapp.ui.my_services

import android.icu.math.BigDecimal
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.servicio.Servicio
import pe.com.valegrei.carwashapp.database.servicio.ServicioDao
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import pe.com.valegrei.carwashapp.network.request.ReqAddServicio
import pe.com.valegrei.carwashapp.network.request.ReqModServicio
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR }
enum class EditStatus { NEW, EDIT, DELETE, NORMAL }
class MyServicesViewModel(
    private val sesionData: SesionData,
    private val servicioDao: ServicioDao,
) : ViewModel() {
    private val TAG = MyServicesViewModel::class.simpleName

    //Datos para editar cuenta
    var precio = MutableLiveData<String>()
    var nombre = MutableLiveData<String>()

    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg
    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status
    private var _editStatus = MutableLiveData<EditStatus>()
    val editStatus: LiveData<EditStatus> = _editStatus

    private var _selectedService = MutableLiveData<Servicio>()
    val selectedService: LiveData<Servicio> = _selectedService

    fun setSelectedService(servicio: Servicio) {
        val selServ = servicio.copy()
        _selectedService.value = selServ
    }

    fun goNuevo() {
        nombre.value = ""
        precio.value = ""
        _editStatus.value = EditStatus.NEW
    }

    fun goEditar() {
        val selServ = selectedService.value!!
        nombre.value = selServ.nombre
        precio.value = selServ.precio.toString()
        _editStatus.value = EditStatus.EDIT
    }

    fun goDelete(){
        _editStatus.value = EditStatus.DELETE
    }

    fun cargarServicios(): Flow<List<Servicio>> {
        val sesion = sesionData.getCurrentSesion();
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

    fun clearEditStatus(){
        _editStatus.value = EditStatus.NORMAL
    }

    fun guardar(){
        when(editStatus.value){
            EditStatus.NEW -> agregarServicio()
            EditStatus.EDIT -> modificarServicio()
            else -> {}
        }
    }

    private fun agregarServicio() {
        val nombre = (this.nombre.value ?: "").trim()
        val precio = (this.precio.value ?: "").trim()
        if (validar(nombre, precio)) {
            agregarServicio(nombre, precio)
        }
    }

    private fun modificarServicio() {
        val nombre = (this.nombre.value ?: "").trim()
        val precio = (this.precio.value ?: "").trim()
        if (validar(nombre, precio)) {
            val modServicio = selectedService.value!!
            modServicio.nombre = nombre;
            modServicio.precio = BigDecimal(precio)
            modificarServicio(modServicio)
        }
    }

    fun eliminarServicio() {
        val modServicio = selectedService.value!!
        modServicio.estado = false//!modServicio.estado
        modificarServicio(modServicio)
    }

    private fun validar(nombre: String, precio: String): Boolean {
        _errMsg.value = ""
        if (nombre.isEmpty()) {
            _errMsg.value = "Campo nombre vacío"
            return false
        }
        if (precio.isEmpty()) {
            _errMsg.value = "Campo precio vacío"
            return false
        }
        return true
    }

    private fun agregarServicio(nombre: String, precio: String) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroServicios()
            val reqAddServicio = ReqAddServicio(nombre, BigDecimal(precio));
            //guarda lo cambiado
            Api.retrofitService.agregarServicio(
                sesion?.usuario?.id!!, reqAddServicio,
                sesion.getTokenBearer()
            )
            //Trae los cambios
            descargarServicios(sesion, lastSincro)
            _editStatus.value = EditStatus.NORMAL
            _status.value = Status.SUCCESS
        }
    }

    private fun modificarServicio(modServicio: Servicio) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroServicios()
            val reqModServicio = ReqModServicio(
                modServicio.id,
                modServicio.nombre,
                modServicio.precio,
                modServicio.estado
            )
            //guarda lo cambiado
            Api.retrofitService.modificarServicio(
                sesion?.usuario?.id!!, reqModServicio,
                sesion.getTokenBearer()
            )
            //Trae los cambios
            descargarServicios(sesion, lastSincro)
            _editStatus.value = EditStatus.NORMAL
            _status.value = Status.SUCCESS
        }
    }


    private suspend fun descargarServicios(sesion: Sesion?, lastSincro: Date) {
        val res = Api.retrofitService.obtenerServicios(
            sesion?.usuario?.id!!,
            lastSincro,
            sesion.getTokenBearer()
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