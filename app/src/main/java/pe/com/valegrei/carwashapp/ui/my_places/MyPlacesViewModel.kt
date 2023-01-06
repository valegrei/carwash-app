package pe.com.valegrei.carwashapp.ui.my_places

import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.direccion.Direccion
import pe.com.valegrei.carwashapp.database.direccion.DireccionDao
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.database.ubigeo.Departamento
import pe.com.valegrei.carwashapp.database.ubigeo.Distrito
import pe.com.valegrei.carwashapp.database.ubigeo.Provincia
import pe.com.valegrei.carwashapp.database.ubigeo.UbigeoDao
import pe.com.valegrei.carwashapp.database.usuario.TipoUsuario
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR }
enum class EditStatus { EXIT, EDIT, NEW, VIEW }
enum class GoStatus { GO_ADD, SHOW_DELETE, NORMAL }
class MyPlacesViewModel(
    private val sesionData: SesionData,
    private val direccionDao: DireccionDao,
    private val ubigeoDao: UbigeoDao,
) : ViewModel() {
    private val TAG = MyPlacesViewModel::class.simpleName

    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg
    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status
    private var _editStatus = MutableLiveData<EditStatus>()
    val editStatus: LiveData<EditStatus> = _editStatus
    private var _goStatus = MutableLiveData<GoStatus>()
    val goStatus: LiveData<GoStatus> = _goStatus
    private var _mostrarEditar = MutableLiveData<Boolean>()
    val mostrarEditar: LiveData<Boolean> = _mostrarEditar

    private var _selectedDepartamento = MutableLiveData<Departamento>()
    val selectedDepartamento: LiveData<Departamento> = _selectedDepartamento
    private var _selectedProvincia = MutableLiveData<Provincia>()
    val selectedProvincia: LiveData<Provincia> = _selectedProvincia
    private var _selectedDistrito = MutableLiveData<Distrito>()
    val selectedDistrito: LiveData<Distrito> = _selectedDistrito
    val direccion = MutableLiveData<String>();
    private var _selectedLatLng = MutableLiveData<LatLng?>()
    val selectedLatLng: LiveData<LatLng?> = _selectedLatLng

    private var _departamentos = MutableLiveData<List<Departamento>>()
    val departamentos: LiveData<List<Departamento>> = _departamentos
    private var _provincias = MutableLiveData<List<Provincia>>()
    val provincias: LiveData<List<Provincia>> = _provincias
    private var _distritos = MutableLiveData<List<Distrito>>()
    val distritos: LiveData<List<Distrito>> = _distritos

    private var _selectedDireccion = MutableLiveData<Direccion>()
    val selectedDireccion: LiveData<Direccion> = _selectedDireccion


    fun nuevoDireccion() {
        _selectedLatLng.value = null
        _selectedDepartamento.value = Departamento(0,"")
        _selectedProvincia.value = Provincia(0,"",0)
        _selectedDistrito.value = Distrito(0,"",0,"")
        cargarDepartamentos()
        _provincias.value = listOf()
        _distritos.value = listOf()
        this.direccion.value = ""
        _errMsg.value = ""
        _editStatus.value = EditStatus.NEW
        _mostrarEditar.value = true
    }

    fun verDireccion(direccion: Direccion) {
        _selectedDireccion.value = direccion
        _selectedLatLng.value = LatLng(direccion.latitud.toDouble(),direccion.longitud.toDouble())
        _errMsg.value = ""
        _editStatus.value = EditStatus.VIEW
        _mostrarEditar.value = false
    }

    fun editarDireccion() {
        val direccion = _selectedDireccion.value!!
        cargarUbigeo(direccion)
        this.direccion.value = direccion.direccion
        _errMsg.value = ""
        _editStatus.value = EditStatus.EDIT
        _mostrarEditar.value = true
    }

    fun isDistrib(): Boolean {
        val sesion = sesionData.getCurrentSesion()
        return sesion?.usuario?.idTipoUsuario == TipoUsuario.DISTR.id
    }

    fun setSelectedLatLng(latLng: LatLng?){
        _selectedLatLng.value = latLng
    }

    fun cargarUbigeo(direccion: Direccion){
        viewModelScope.launch {
            val departamentos = ubigeoDao.obtenerDepartamentos()
            val dep = departamentos.find {it.departamento == direccion.departamento}
            _selectedDepartamento.value = dep?: Departamento(idDepartamento = 0,departamento="")
            _departamentos.value = departamentos

            val provincias = ubigeoDao.obtenerProvincia(selectedDepartamento.value?.idDepartamento!!)
            val prov = provincias.find {it.provincia == direccion.provincia}
            _selectedProvincia.value = prov?:Provincia(idProvincia = 0,provincia="", idDepartamento = 0)
            _provincias.value = provincias

            val distritos = ubigeoDao.obtenerDistrito(selectedProvincia.value?.idProvincia!!)
            val dis = distritos.find {it.distrito == direccion.distrito}
            _selectedDistrito.value = dis?:Distrito(idDistrito = 0,distrito="", idProvincia = 0, codigo = "")
            _distritos.value = distritos
        }
    }

    fun cargarDepartamentos() {
        viewModelScope.launch {
            _departamentos.value = ubigeoDao.obtenerDepartamentos()
        }
    }

    fun setSelectedDepartamento(selectedDepartamento: Departamento){
        _selectedDepartamento.value = selectedDepartamento
    }

    fun cargarProvincias(departamento: Departamento) {
        viewModelScope.launch {
            _provincias.value = ubigeoDao.obtenerProvincia(departamento.idDepartamento)
        }
    }

    fun setSelectedProvincia(selectedProvincia: Provincia){
        _selectedProvincia.value = selectedProvincia
    }

    fun cargarDistritos(provincia: Provincia) {
        viewModelScope.launch {
            _distritos.value = ubigeoDao.obtenerDistrito(provincia.idProvincia)
        }
    }

    fun setSelectedDistrito(selectedDistrito: Distrito){
        _selectedDistrito.value = selectedDistrito
    }

    fun cargarDirecciones(): Flow<List<Direccion>> {
        val sesion = sesionData.getCurrentSesion()
        return direccionDao.obtenerDirecciones(sesion?.usuario?.id!!)
    }

    fun descargarDirecciones() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroDirecciones()
            descargarDirecciones(sesion, lastSincro)
            _status.value = Status.SUCCESS
        }
    }

    private suspend fun descargarDirecciones(sesion: Sesion?, lastSincro: Date) {
        val res = Api.retrofitService.obtenerDirecciones(
            sesion?.usuario?.id!!,
            lastSincro,
            sesion.getTokenBearer()
        )
        val direcciones = res.data.direcciones
        if (direcciones.isNotEmpty()) {
            direccionDao.guardarDirecciones(direcciones)
        }
        sesionData.saveLastSincroDirecciones(res.timeStamp)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        _errMsg.value = exceptionError.message
        _status.value = Status.ERROR
    }
}

class MyPlacesViewModelFactory(
    private val sesionData: SesionData,
    private val direccionDao: DireccionDao,
    private val ubigeoDao: UbigeoDao,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPlacesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyPlacesViewModel(sesionData, direccionDao, ubigeoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}