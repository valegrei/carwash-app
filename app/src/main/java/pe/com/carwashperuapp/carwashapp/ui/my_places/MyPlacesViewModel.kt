package pe.com.carwashperuapp.carwashapp.ui.my_places

import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.direccion.Direccion
import pe.com.carwashperuapp.carwashapp.database.direccion.DireccionDao
import pe.com.carwashperuapp.carwashapp.database.direccion.TipoDireccion
import pe.com.carwashperuapp.carwashapp.database.sesion.Sesion
import pe.com.carwashperuapp.carwashapp.database.ubigeo.Departamento
import pe.com.carwashperuapp.carwashapp.database.ubigeo.Distrito
import pe.com.carwashperuapp.carwashapp.database.ubigeo.Provincia
import pe.com.carwashperuapp.carwashapp.database.ubigeo.UbigeoDao
import pe.com.carwashperuapp.carwashapp.database.usuario.TipoUsuario
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import pe.com.carwashperuapp.carwashapp.network.request.ReqDireccion
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

    //errores
    private var _errDepartamento = MutableLiveData<String?>()
    val errDepartamento: LiveData<String?> = _errDepartamento
    private var _errProvincia = MutableLiveData<String?>()
    val errProvincia: LiveData<String?> = _errProvincia
    private var _errDistrito = MutableLiveData<String?>()
    val errDistrito: LiveData<String?> = _errDistrito
    private var _errDireccion = MutableLiveData<String?>()
    val errDireccion: LiveData<String?> = _errDireccion
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
    private var _isDistrib = MutableLiveData<Boolean>()
    val isDistrib: LiveData<Boolean> = _isDistrib

    //seleccionados
    private var _selectedDepartamento = MutableLiveData<Departamento>()
    val selectedDepartamento: LiveData<Departamento> = _selectedDepartamento
    private var _selectedProvincia = MutableLiveData<Provincia>()
    val selectedProvincia: LiveData<Provincia> = _selectedProvincia
    private var _selectedDistrito = MutableLiveData<Distrito>()
    val selectedDistrito: LiveData<Distrito> = _selectedDistrito
    val direccion = MutableLiveData<String>()
    private var _selectedLatLng = MutableLiveData<LatLng?>()
    val selectedLatLng: LiveData<LatLng?> = _selectedLatLng
    private var _selectedDireccion = MutableLiveData<Direccion>()
    val selectedDireccion: LiveData<Direccion> = _selectedDireccion
    private var _selectedTipo = MutableLiveData<Int?>()
    val selectedTipo: LiveData<Int?> = _selectedTipo

    //campos
    private var _departamentos = MutableLiveData<List<Departamento>>()
    val departamentos: LiveData<List<Departamento>> = _departamentos
    private var _provincias = MutableLiveData<List<Provincia>>()
    val provincias: LiveData<List<Provincia>> = _provincias
    private var _distritos = MutableLiveData<List<Distrito>>()
    val distritos: LiveData<List<Distrito>> = _distritos

    fun nuevoDireccion() {
        _selectedLatLng.value = null
        _selectedDepartamento.value = Departamento(0, "")
        _selectedProvincia.value = Provincia(0, "", 0)
        _selectedDistrito.value = Distrito(0, "", 0, "")
        cargarDepartamentos()
        _selectedTipo.value = 0
        _provincias.value = listOf()
        _distritos.value = listOf()
        this.direccion.value = ""
        _errMsg.value = ""
        _editStatus.value = EditStatus.NEW
        _mostrarEditar.value = true
    }

    fun verDireccion(direccion: Direccion) {
        _selectedDireccion.value = direccion
        _selectedLatLng.value = LatLng(direccion.latitud.toDouble(), direccion.longitud.toDouble())
        _errMsg.value = ""
        _editStatus.value = EditStatus.VIEW
        _mostrarEditar.value = false
    }

    fun editarDireccion() {
        val direccion = _selectedDireccion.value!!
        cargarUbigeo(direccion)
        this.direccion.value = direccion.direccion
        _selectedTipo.value = direccion.tipo
        _errMsg.value = ""
        _editStatus.value = EditStatus.EDIT
        _mostrarEditar.value = true
    }

    init {
        setIsDistrib()
    }

    private fun setIsDistrib() {
        val sesion = sesionData.getCurrentSesion()
        _isDistrib.value = sesion?.usuario?.idTipoUsuario == TipoUsuario.DISTR.id
    }

    fun selectCasa() {
        _selectedTipo.value = TipoDireccion.CASA.id
    }

    fun selectOficina() {
        _selectedTipo.value = TipoDireccion.OFICINA.id
    }

    fun selectOtro() {
        _selectedTipo.value = TipoDireccion.OTRO.id
    }

    fun setSelectedLatLng(latLng: LatLng?) {
        _selectedLatLng.value = latLng
    }

    fun cargarUbigeo(direccion: Direccion) {
        viewModelScope.launch {
            val departamentos = ubigeoDao.obtenerDepartamentos()
            val dep = departamentos.find { it.departamento == direccion.departamento }
            _selectedDepartamento.value = dep ?: Departamento(0, "")
            _departamentos.value = departamentos

            val provincias =
                ubigeoDao.obtenerProvincia(selectedDepartamento.value?.idDepartamento!!)
            val prov = provincias.find { it.provincia == direccion.provincia }
            _selectedProvincia.value =
                prov ?: Provincia(0, "", 0)
            _provincias.value = provincias

            val distritos = ubigeoDao.obtenerDistrito(selectedProvincia.value?.idProvincia!!)
            val dis = distritos.find { it.distrito == direccion.distrito }
            _selectedDistrito.value =
                dis ?: Distrito(0, "", 0, "")
            _distritos.value = distritos
        }
    }

    fun cargarDepartamentos() {
        viewModelScope.launch {
            _departamentos.value = ubigeoDao.obtenerDepartamentos()
        }
    }

    fun setSelectedDepartamento(selectedDepartamento: Departamento) {
        _selectedDepartamento.value = selectedDepartamento
    }

    fun cargarProvincias(departamento: Departamento) {
        viewModelScope.launch {
            _provincias.value = ubigeoDao.obtenerProvincia(departamento.idDepartamento)
        }
    }

    fun setSelectedProvincia(selectedProvincia: Provincia) {
        _selectedProvincia.value = selectedProvincia
    }

    fun cargarDistritos(provincia: Provincia) {
        viewModelScope.launch {
            _distritos.value = ubigeoDao.obtenerDistrito(provincia.idProvincia)
        }
    }

    fun setSelectedDistrito(selectedDistrito: Distrito) {
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
            lastSincro,
            sesion?.getTokenBearer()!!
        )
        val direcciones = res.data.direcciones
        if (direcciones.isNotEmpty()) {
            direccionDao.guardarDirecciones(direcciones)
        }
        sesionData.saveLastSincroDirecciones(res.timeStamp)
    }

    private fun crearDireccion() {
        val direccion = (this.direccion.value ?: "").trim()
        if (validar(direccion)) {
            crearDireccion(direccion)
        }
    }

    private fun crearDireccion(direccion: String) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()

            val departamento = selectedDepartamento.value?.departamento
            val provincia = selectedProvincia.value?.provincia
            val distrito = selectedDistrito.value?.distrito
            val ubigeo = selectedDistrito.value?.codigo
            val latitud = selectedLatLng.value?.latitude.toString()
            val longitud = selectedLatLng.value?.longitude.toString()
            val tipo = selectedTipo.value!!
            val reqDireccion = ReqDireccion(
                departamento,
                provincia,
                distrito,
                ubigeo,
                direccion,
                latitud,
                longitud,
                tipo
            )

            Api.retrofitService.agregarDireccion(
                reqDireccion,
                sesion?.getTokenBearer()!!
            )

            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }

    private fun clearErrs() {
        _errDepartamento.value = null
        _errProvincia.value = null
        _errDistrito.value = null
        _errDireccion.value = null
        _errMsg.value = null
    }

    private fun validar(direccion: String): Boolean {
        var res = true
        clearErrs()
        _errDepartamento.value = ""
        _errProvincia.value = ""
        _errDistrito.value = ""
        _errDireccion.value = ""
        _errMsg.value = ""
        if (isDistrib.value!!) {
            if ((selectedDepartamento.value?.departamento ?: "").isEmpty()) {
                _errDepartamento.value = "Seleccione Departamento"
                res = false
            }
            if ((selectedProvincia.value?.provincia ?: "").isEmpty()) {
                _errProvincia.value = "Seleccione Provincia"
                res = false
            }
            if ((selectedDistrito.value?.distrito ?: "").isEmpty()) {
                _errDistrito.value = "Seleccione Distrito"
                res = false
            }
        } else {
            if ((selectedTipo.value ?: 0) == 0) {
                _errMsg.value = "Seleccione tipo de dirección"
                res = false
            }
        }
        if (direccion.isEmpty()) {
            _errDireccion.value = "Ingrese su dirección"
            res = false
        }
        if (selectedLatLng.value == null) {
            _errMsg.value = "Seleccione la dirección en el mapa"
            res = false
        }
        return res
    }

    fun eliminarDireccion() {
        val idDireccion = selectedDireccion.value?.id!!
        eliminarDireccion(idDireccion)
    }

    private fun eliminarDireccion(idDireccion: Int) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()

            Api.retrofitService.eliminarDireccion(
                idDireccion,
                sesion?.getTokenBearer()!!
            )

            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }

    private fun actualizarDireccion() {
        val idDireccion = selectedDireccion.value?.id!!
        val direccion = (this.direccion.value ?: "").trim()
        if (validar(direccion)) {
            actualizarDireccion(idDireccion, direccion)
        }
    }

    private fun actualizarDireccion(idDireccion: Int, direccion: String) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()

            val departamento = selectedDepartamento.value?.departamento
            val provincia = selectedProvincia.value?.provincia
            val distrito = selectedDistrito.value?.distrito
            val ubigeo = selectedDistrito.value?.codigo
            val latitud = selectedLatLng.value?.latitude.toString()
            val longitud = selectedLatLng.value?.longitude.toString()
            val tipo = selectedTipo.value!!
            val reqDireccion = ReqDireccion(
                departamento,
                provincia,
                distrito,
                ubigeo,
                direccion,
                latitud,
                longitud,
                tipo
            )

            Api.retrofitService.modificarDireccion(
                idDireccion,
                reqDireccion,
                sesion?.getTokenBearer()!!
            )

            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }

    fun guardarDireccion() {
        when (editStatus.value) {
            EditStatus.EDIT -> actualizarDireccion()
            EditStatus.NEW -> crearDireccion()
            else -> {}
        }
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