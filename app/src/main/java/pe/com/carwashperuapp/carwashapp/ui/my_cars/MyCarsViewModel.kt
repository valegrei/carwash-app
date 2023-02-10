package pe.com.carwashperuapp.carwashapp.ui.my_cars

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.sesion.Sesion
import pe.com.carwashperuapp.carwashapp.database.vehiculo.Vehiculo
import pe.com.carwashperuapp.carwashapp.database.vehiculo.VehiculoDao
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import pe.com.carwashperuapp.carwashapp.ui.announcement.TuplaImageEdit
import java.io.File
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR }
enum class EditStatus { EXIT, EDIT, NEW, VIEW }
enum class GoStatus { GO_ADD, SHOW_DELETE, NORMAL }
enum class AddFotoStatus { LAUNCH, NORMAL }
class MyCarsViewModel(
    private val sesionData: SesionData,
    private val vehiculoDao: VehiculoDao,
) : ViewModel() {
    private val TAG = MyCarsViewModel::class.simpleName

    //errores
    private var _errMarca = MutableLiveData<String?>()
    val errMarca: LiveData<String?> = _errMarca
    private var _errModelo = MutableLiveData<String?>()
    val errModelo: LiveData<String?> = _errModelo
    private var _errYear = MutableLiveData<String?>()
    val errYear: LiveData<String?> = _errYear
    private var _errPlaca = MutableLiveData<String?>()
    val errPlaca: LiveData<String?> = _errPlaca
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
    private var _addFotoStatud = MutableLiveData<AddFotoStatus>()
    val addFotoStatus: LiveData<AddFotoStatus> = _addFotoStatud

    //seleccionados
    private var _selectedVehiculo = MutableLiveData<Vehiculo>()
    val selectedVehiculo: LiveData<Vehiculo> = _selectedVehiculo

    //campos
    var marca = MutableLiveData<String>()
    var modelo = MutableLiveData<String>()
    var year = MutableLiveData<String>()
    var placa = MutableLiveData<String>()
    private var _borrarFoto = MutableLiveData<Boolean>()
    val borrarFoto: LiveData<Boolean> = _borrarFoto
    private var _imagen = MutableLiveData<TuplaImageEdit>()
    val imagen: LiveData<TuplaImageEdit> = _imagen

    fun nuevoVehiculo() {
        modelo.value = ""
        marca.value = ""
        year.value = ""
        placa.value = ""
        clearErrs()
        _imagen.value = TuplaImageEdit(null, null, null)
        _borrarFoto.value = false
        _editStatus.value = EditStatus.NEW
        _mostrarEditar.value = true
    }

    fun verVehiculo(vehiculo: Vehiculo) {
        _selectedVehiculo.value = vehiculo
        clearErrs()
        _imagen.value = TuplaImageEdit(vehiculo.getUrlArchivo(), null, null)
        _editStatus.value = EditStatus.VIEW
        _mostrarEditar.value = false
    }

    fun editarVehiculo() {
        val vehiculo = _selectedVehiculo.value!!
        modelo.value = vehiculo.modelo
        marca.value = vehiculo.marca
        year.value = vehiculo.year.toString()
        placa.value = vehiculo.placa
        _borrarFoto.value = false
        clearErrs()
        _editStatus.value = EditStatus.EDIT
        _mostrarEditar.value = true
    }

    fun cargarVehiculos(): Flow<List<Vehiculo>> {
        val sesion = sesionData.getCurrentSesion()
        return vehiculoDao.obtenerVehiculos(sesion?.usuario?.id!!)
    }

    fun descargarVehiculos() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroVehiculos()
            descargarVehiculos(sesion, lastSincro)
            _status.value = Status.SUCCESS
        }
    }

    fun nuevaFoto(uriFile: Uri?, pathFile: String?) {
        _imagen.value = TuplaImageEdit(null, uriFile, pathFile)
        _borrarFoto.value = false
    }

    fun eliminarFoto() {
        _imagen.value = TuplaImageEdit(null, null, null)
        _borrarFoto.value = true
    }

    fun mostrarEliminarFoto(): Boolean {
        val editFoto = imagen.value!!
        if (borrarFoto.value!!)
            return false
        if ((editFoto.urlImagen ?: "").isNotEmpty() || editFoto.uriFile != null) {
            return true
        }
        return false
    }

    fun lanzarAddFoto() {
        _addFotoStatud.value = AddFotoStatus.LAUNCH
    }

    fun ocultarAddFoto() {
        _addFotoStatud.value = AddFotoStatus.NORMAL
    }

    private suspend fun descargarVehiculos(sesion: Sesion?, lastSincro: String?) {
        val res = Api.retrofitService.obtenerVehiculos(
            lastSincro,
            sesion?.getTokenBearer()!!
        )
        val vehiculos = res.data.vehiculos
        if (vehiculos.isNotEmpty()) {
            vehiculoDao.guardarVehiculos(vehiculos)
        }
        sesionData.saveLastSincroVehiculos(res.timeStamp)
    }

    private fun crearVehiculo() {
        val marca = (this.marca.value ?: "").trim()
        val modelo = (this.modelo.value ?: "").trim()
        val year = (this.year.value ?: "").trim()
        val placa = (this.placa.value ?: "").trim()
        if (validar(marca, modelo, year, placa)) {
            crearVehiculo(marca, modelo, year, placa)
        }
    }

    private fun crearVehiculo(marca: String, modelo: String, year: String, placa: String) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()

            val rbMarca = RequestBody.create(MediaType.parse("text/plain"), marca)
            val rbModelo = RequestBody.create(MediaType.parse("text/plain"), modelo)
            val rbYear = RequestBody.create(MediaType.parse("text/plain"), year)
            val rbPlaca = RequestBody.create(MediaType.parse("text/plain"), placa)

            var rbImagen: MultipartBody.Part? = null
            if (imagen.value?.pathFile != null) {
                val file = File(imagen.value?.pathFile!!)

                if (file.exists())
                    rbImagen = MultipartBody.Part.createFormData(
                        "imagen",
                        file.name,
                        RequestBody.create(MediaType.parse("image/*"), file)
                    )
            }

            Api.retrofitService.agregarVehiculo(
                rbMarca,
                rbModelo,
                rbYear,
                rbPlaca,
                rbImagen,
                sesion?.getTokenBearer()!!
            )
            descargarVehiculos(sesion, sesionData.getLastSincroVehiculos())
            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }

    private fun clearErrs() {
        _errMarca.value = null
        _errModelo.value = null
        _errYear.value = null
        _errMsg.value = null
        _errPlaca.value = null
    }

    private fun validar(marca: String, modelo: String, year: String, placa: String): Boolean {
        var res = true
        clearErrs()
        if (marca.isEmpty()) {
            _errMarca.value = "Falta marca del auto"
            res = false
        }
        if (modelo.isEmpty()) {
            _errModelo.value = "Falta modelo del auto"
            res = false
        }
        if (year.isEmpty()) {
            _errYear.value = "Falta año del auto"
            res = false
        } else if (year.toInt() < 1950 || year.toInt() > Calendar.getInstance()
                .get(Calendar.YEAR)
        ) {
            _errYear.value = "Ingrese un año válido"
            res = false
        }
        if (placa.isEmpty()) {
            _errModelo.value = "Falta placa del auto"
            res = false
        }
        return res
    }

    fun eliminarVehiculo() {
        val idVehiculo = selectedVehiculo.value?.id!!
        eliminarVehiculo(idVehiculo)
    }

    private fun eliminarVehiculo(idVehiculo: Int) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()

            Api.retrofitService.eliminarVehiculo(
                idVehiculo,
                sesion?.getTokenBearer()!!
            )

            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }

    private fun actualizarVehiculo() {
        val idVehiculo = selectedVehiculo.value?.id!!
        val marca = (this.marca.value ?: "").trim()
        val modelo = (this.modelo.value ?: "").trim()
        val year = (this.year.value ?: "").trim()
        val placa = (this.placa.value ?: "").trim()
        val borrarFoto = this.borrarFoto.value!!
        if (validar(marca, modelo, year, placa)) {
            actualizarVehiculo(idVehiculo, marca, modelo, year, placa, borrarFoto)
        }
    }

    private fun actualizarVehiculo(
        idVehiculo: Int,
        marca: String,
        modelo: String,
        year: String,
        placa: String,
        borrarFoto: Boolean
    ) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()


            val rbMarca = RequestBody.create(MediaType.parse("text/plain"), marca)
            val rbModelo = RequestBody.create(MediaType.parse("text/plain"), modelo)
            val rbYear = RequestBody.create(MediaType.parse("text/plain"), year)
            val rbPlaca = RequestBody.create(MediaType.parse("text/plain"), placa)
            val rbBorrarFoto =
                RequestBody.create(MediaType.parse("text/plain"), borrarFoto.toString())

            var rbImagen: MultipartBody.Part? = null
            if (imagen.value?.pathFile != null) {
                val file = File(imagen.value?.pathFile!!)

                if (file.exists())
                    rbImagen = MultipartBody.Part.createFormData(
                        "imagen",
                        file.name,
                        RequestBody.create(MediaType.parse("image/*"), file)
                    )
            }

            Api.retrofitService.modificarVehiculo(
                idVehiculo,
                rbMarca,
                rbModelo,
                rbYear,
                rbPlaca,
                rbBorrarFoto,
                rbImagen,
                sesion?.getTokenBearer()!!
            )

            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }

    fun guardarVehiculo() {
        when (editStatus.value) {
            EditStatus.EDIT -> actualizarVehiculo()
            EditStatus.NEW -> crearVehiculo()
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

class MyCarsViewModelFactory(
    private val sesionData: SesionData,
    private val vehiculoDao: VehiculoDao,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyCarsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyCarsViewModel(sesionData, vehiculoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}