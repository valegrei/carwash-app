package pe.com.valegrei.carwashapp

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.database.usuario.TipoDocumento
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import java.io.File

enum class SesionStatus { NORMAL, CLOSED }
enum class EditStatus { LOADING, SUCCESS, ERROR, NORMAL }
enum class AddFotoStatus { LAUNCH, NORMAL}

class MainViewModel(private val sesionData: SesionData) : ViewModel() {
    private val TAG = MainViewModel::class.simpleName
    private var _sesionStatus = MutableLiveData<SesionStatus>()
    val sesionStatus: LiveData<SesionStatus> = _sesionStatus
    private var _addFotoStatud = MutableLiveData<AddFotoStatus>()
    val addFotoStatus : LiveData<AddFotoStatus> = _addFotoStatud
    private var _sesion = MutableLiveData<Sesion>()
    val sesion: LiveData<Sesion> = _sesion

    private var _ternaFoto = MutableLiveData<EstrEditFoto>()
    val ternaFoto: LiveData<EstrEditFoto> = _ternaFoto

    //Datos para editar cuenta
    var nombres = MutableLiveData<String>()
    var apePat = MutableLiveData<String>()
    var apeMat = MutableLiveData<String>()
    var razSoc = MutableLiveData<String>()
    var idTipoDoc = MutableLiveData<Int>()
    var nroDoc = MutableLiveData<String>()
    var nroCel1 = MutableLiveData<String>()
    var nroCel2 = MutableLiveData<String>()

    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg
    private var _status = MutableLiveData<EditStatus>()
    val status: LiveData<EditStatus> = _status

    fun getTipoDoc(): String {
        return when (idTipoDoc.value!!) {
            TipoDocumento.DNI.id -> TipoDocumento.DNI.nombre
            TipoDocumento.RUC.id -> TipoDocumento.RUC.nombre
            TipoDocumento.CEXT.id -> TipoDocumento.CEXT.nombre
            else -> ""
        }
    }

    init {
        _sesionStatus.value = SesionStatus.NORMAL
        cargarSesion()
    }

    fun cargarSesion() {
        _sesion.value = sesionData.getCurrentSesion()
    }

    fun nuevaFoto(uri: Uri?,path: String?) {
        val editFoto = ternaFoto.value
        editFoto?.uriFile = uri
        editFoto?.filePath = path
        editFoto?.eliminarFoto = false
        _ternaFoto.value = editFoto!!
    }

    fun eliminarFoto() {
        val editFoto = ternaFoto.value
        editFoto?.uriFile = null
        editFoto?.filePath = null
        editFoto?.eliminarFoto = true
        _ternaFoto.value = editFoto!!
    }

    fun mostrarEliminarFoto(): Boolean{
        val editFoto = ternaFoto.value
        if(editFoto?.eliminarFoto!!)
            return false
        if((editFoto.urlOriginal?:"").isNotEmpty() || editFoto.uriFile!=null){
            return true
        }
        return false
    }

    fun lanzarAddFoto(){
        _addFotoStatud.value = AddFotoStatus.LAUNCH
    }

    fun ocultarAddFoto(){
        _addFotoStatud.value = AddFotoStatus.NORMAL
    }

    fun cargarCamposEdit() {
        _addFotoStatud.value = AddFotoStatus.NORMAL
        _status.value = EditStatus.NORMAL
        nombres.value = sesion.value?.usuario?.nombres!!
        apePat.value = sesion.value?.usuario?.apellidoPaterno!!
        apeMat.value = sesion.value?.usuario?.apellidoMaterno!!
        razSoc.value = sesion.value?.usuario?.razonSocial!!
        idTipoDoc.value = sesion.value?.usuario?.idTipoDocumento!!
        nroDoc.value = sesion.value?.usuario?.nroDocumento!!
        nroDoc.value = sesion.value?.usuario?.nroDocumento!!
        nroCel1.value = sesion.value?.usuario?.nroCel1!!
        nroCel2.value = sesion.value?.usuario?.nroCel2!!
        _ternaFoto.value = EstrEditFoto(sesion.value?.usuario?.getURLFoto(),null,null,false)
    }

    fun guardarCambios() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = EditStatus.LOADING
            //armar usuario con cambios
            val usuario = sesion.value?.usuario!!
            val sesion = sesion.value!!
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            val rbNombres = RequestBody.create(MediaType.parse("text/plain"), nombres.value ?: "")
            val rbApePat = RequestBody.create(MediaType.parse("text/plain"), apePat.value ?: "")
            val rbApeMat = RequestBody.create(MediaType.parse("text/plain"), apeMat.value ?: "")
            val rbRazSoc = RequestBody.create(MediaType.parse("text/plain"), razSoc.value ?: "")
            val rbIdTipoDoc =
                RequestBody.create(MediaType.parse("text/plain"), (idTipoDoc.value ?: 0).toString())
            val rbNroDoc = RequestBody.create(MediaType.parse("text/plain"), nroDoc.value ?: "")
            val rbNroCel1 = RequestBody.create(MediaType.parse("text/plain"), nroCel1.value ?: "")
            val rbNroCel2 = RequestBody.create(MediaType.parse("text/plain"), nroCel2.value ?: "")

            var rbFoto: MultipartBody.Part? = null
            if (ternaFoto.value?.filePath != null) {
                val file = File(ternaFoto.value?.filePath!!)

                if (file.exists())
                    rbFoto = MultipartBody.Part.createFormData(
                        "foto",
                        file.name,
                        RequestBody.create(MediaType.parse("image/*"), file)
                    )
            }
            var rbEliminarFoto: RequestBody? = null
            if (ternaFoto.value?.eliminarFoto!!) {
                rbEliminarFoto =
                    RequestBody.create(MediaType.parse("text/plain"), ternaFoto.value?.eliminarFoto.toString())
            }

            //guardando en server
            val res = Api.retrofitService.actualizarUsuario(
                usuario.id!!,
                rbNombres,
                rbApePat,
                rbApeMat,
                rbRazSoc,
                rbIdTipoDoc,
                rbNroDoc,
                rbNroCel1,
                rbNroCel2,
                rbFoto,
                rbEliminarFoto,
                sesion.getTokenBearer()
            )
            //guardando en local
            sesion.usuario = res.data.usuario
            sesionData.saveSesion(sesion)
            cargarSesion()
            _status.value = EditStatus.SUCCESS
        }
    }

    fun cerrarSesion() {
        sesionData.closeSesion()
        _sesionStatus.value = SesionStatus.CLOSED
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        _errMsg.value = exceptionError.message
        _status.value = EditStatus.ERROR
    }
}

class EstrEditFoto(
    var urlOriginal: String? = null,
    var uriFile: Uri? = null,
    var filePath: String? = null,
    var eliminarFoto: Boolean = false
)

class MainViewModelFactory(
    private val sesionData: SesionData
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(sesionData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}