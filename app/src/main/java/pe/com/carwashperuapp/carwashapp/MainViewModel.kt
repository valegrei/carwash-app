package pe.com.carwashperuapp.carwashapp

import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.sesion.Sesion
import pe.com.carwashperuapp.carwashapp.database.usuario.TipoDocumento
import pe.com.carwashperuapp.carwashapp.database.usuario.TipoUsuario
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import java.io.File

enum class SesionStatus { NORMAL, CLOSED }
enum class EditStatus { LOADING, SUCCESS, ERROR, NORMAL }
enum class AddFotoStatus { LAUNCH, NORMAL }

class MainViewModel(private val sesionData: SesionData) : ViewModel() {
    private val TAG = MainViewModel::class.simpleName
    private var _sesionStatus = MutableLiveData<SesionStatus>()
    val sesionStatus: LiveData<SesionStatus> = _sesionStatus
    private var _sesion = MutableLiveData<Sesion>()
    val sesion: LiveData<Sesion> = _sesion

    //Datos para editar cuenta
    var nombres = MutableLiveData<String>()
    var apePat = MutableLiveData<String>()
    var apeMat = MutableLiveData<String>()
    var razSoc = MutableLiveData<String>()
    var nroDoc = MutableLiveData<String>()
    var nroCel1 = MutableLiveData<String>()
    var nroCel2 = MutableLiveData<String>()
    var acercaDe = MutableLiveData<String?>()
    private var _tiposDoc = MutableLiveData<Array<TipoDocumento>>()
    var tiposDoc: LiveData<Array<TipoDocumento>> = _tiposDoc
    private var _selectedTipoDoc = MutableLiveData<TipoDocumento>()
    var selectedTipoDoc: LiveData<TipoDocumento> = _selectedTipoDoc
    private var _ternaFoto = MutableLiveData<EstrEditFoto>()
    val ternaFoto: LiveData<EstrEditFoto> = _ternaFoto

    //errores
    private var _errMsg = MutableLiveData<String?>()
    val errMsg: LiveData<String?> = _errMsg
    private var _errNombres = MutableLiveData<String?>()
    val errNombres: LiveData<String?> = _errNombres
    private var _errApePat = MutableLiveData<String?>()
    val errApePat: LiveData<String?> = _errApePat
    private var _errApeMat = MutableLiveData<String?>()
    val errApeMat: LiveData<String?> = _errApeMat
    private var _errRazSoc = MutableLiveData<String?>()
    val errRazSoc: LiveData<String?> = _errRazSoc
    private var _errNroDoc = MutableLiveData<String?>()
    val errNroDoc: LiveData<String?> = _errNroDoc
    private var _errNroTelef = MutableLiveData<String?>()
    val errNroTelef: LiveData<String?> = _errNroTelef
    private var _errNroWhatsapp = MutableLiveData<String?>()
    val errNroWhatsapp: LiveData<String?> = _errNroWhatsapp

    //estados
    private var _status = MutableLiveData<EditStatus>()
    val status: LiveData<EditStatus> = _status
    private var _addFotoStatud = MutableLiveData<AddFotoStatus>()
    val addFotoStatus: LiveData<AddFotoStatus> = _addFotoStatud

    init {
        _sesionStatus.value = SesionStatus.NORMAL
        cargarSesion()
    }

    fun mostrarBanner(): Boolean{
        return sesion.value?.usuario?.idTipoUsuario == TipoUsuario.DISTR.id
    }

    fun getTipoPerfilNombre(): String = sesion.value?.usuario?.getTipoPerfilNombre()!!

    fun cargarSesion() {
        _sesion.value = sesionData.getCurrentSesion()
    }

    fun clearErrs() {
        _errMsg.value = null
        _errNombres.value = null
        _errApePat.value = null
        _errApeMat.value = null
        _errRazSoc.value = null
        _errNroDoc.value = null
        _errNroTelef.value = null
        _errNroWhatsapp.value = null
    }

    fun cargarCamposEdit() {
        _status.value = EditStatus.NORMAL
        if (sesion.value?.usuario?.idTipoUsuario == TipoUsuario.DISTR.id) {
            _tiposDoc.value = arrayOf(TipoDocumento.RUC, TipoDocumento.DNI, TipoDocumento.CEXT)
        } else {
            _tiposDoc.value = arrayOf(TipoDocumento.DNI, TipoDocumento.CEXT)
        }
        nombres.value = sesion.value?.usuario?.nombres!!
        apePat.value = sesion.value?.usuario?.apellidoPaterno!!
        apeMat.value = sesion.value?.usuario?.apellidoMaterno!!
        razSoc.value = sesion.value?.usuario?.razonSocial!!
        when (sesion.value?.usuario?.idTipoDocumento) {
            TipoDocumento.RUC.id -> _selectedTipoDoc.value = TipoDocumento.RUC
            TipoDocumento.DNI.id -> _selectedTipoDoc.value = TipoDocumento.DNI
            TipoDocumento.CEXT.id -> _selectedTipoDoc.value = TipoDocumento.CEXT
        }
        nroDoc.value = sesion.value?.usuario?.nroDocumento!!
        nroDoc.value = sesion.value?.usuario?.nroDocumento!!
        nroCel1.value = sesion.value?.usuario?.nroCel1!!
        nroCel2.value = sesion.value?.usuario?.nroCel2!!
        acercaDe.value = sesion.value?.usuario?.acercaDe
        _ternaFoto.value = EstrEditFoto(sesion.value?.usuario?.getURLFoto(),null,null,false)
    }

    fun validar(): Boolean {
        var res = true
        clearErrs()
        if (sesion.value?.usuario?.idTipoUsuario!! == TipoUsuario.DISTR.id) {
            //Logica distribuidor
            if ((razSoc.value ?: "").trim().isEmpty()) {
                _errRazSoc.value = "Campo Razón Social faltante"
                res = false
            }
            val nroCel1 = (this.nroCel1.value ?: "").trim()
            if (nroCel1.trim().isNotEmpty() && !Patterns.PHONE.matcher(nroCel1).matches()) {
                _errNroTelef.value = "Nro. inválido"
                res = false
            }
            val nroCel2 = (this.nroCel2.value ?: "").trim()
            if (nroCel2.isNotEmpty() && !Patterns.PHONE.matcher(nroCel2).matches()) {
                _errNroWhatsapp.value = "Nro. inválido"
                res = false
            }
        } else {
            //Otro usuario
            if ((nombres.value ?: "").trim().isEmpty()) {
                _errNombres.value = "Campo Nombre faltante"
                res = false
            }
            if ((apePat.value ?: "").isEmpty()) {
                _errApePat.value = "Campo Apellido Paterno faltante"
                res = false
            }
        }
        val nroDoc = (this.nroDoc.value ?: "").trim()
        if (nroDoc.isEmpty()) {
            _errNroDoc.value = "Campo Número de Documento faltante"
            res = false
        } else if (nroDoc.length != selectedTipoDoc.value?.digitos) {
            _errNroDoc.value = "Debe tener ${selectedTipoDoc.value?.digitos} dígitos"
            res = false
        }

        return res
    }

    fun guardarCambios() {
        if (validar()) {
            actualizarUsuario()
        }
    }

    private fun actualizarUsuario() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = EditStatus.LOADING
            //armar usuario con cambios
            val sesion = sesion.value!!

            val rbNombres = RequestBody.create(MediaType.parse("text/plain"), nombres.value ?: "")
            val rbApePat = RequestBody.create(MediaType.parse("text/plain"), apePat.value ?: "")
            val rbApeMat = RequestBody.create(MediaType.parse("text/plain"), apeMat.value ?: "")
            val rbRazSoc = RequestBody.create(MediaType.parse("text/plain"), razSoc.value ?: "")
            val rbIdTipoDoc =
                RequestBody.create(
                    MediaType.parse("text/plain"),
                    (selectedTipoDoc.value?.id ?: 0).toString()
                )
            val rbNroDoc = RequestBody.create(MediaType.parse("text/plain"), nroDoc.value ?: "")
            val rbNroCel1 = RequestBody.create(MediaType.parse("text/plain"), nroCel1.value ?: "")
            val rbNroCel2 = RequestBody.create(MediaType.parse("text/plain"), nroCel2.value ?: "")
            val rbAcercaDe = RequestBody.create(MediaType.parse("text/plain"), acercaDe.value ?: "")

            var rbFoto: MultipartBody.Part? = null
            if (ternaFoto.value?.filePath != null) {
                val file = File(ternaFoto.value?.filePath!!)

                if (file.exists())
                    rbFoto = MultipartBody.Part.createFormData(
                        "banner",
                        file.name,
                        RequestBody.create(MediaType.parse("image/*"), file)
                    )
            }
            var rbEliminarFoto: RequestBody? = null
            if (ternaFoto.value?.eliminarFoto!!) {
                rbEliminarFoto =
                    RequestBody.create(
                        MediaType.parse("text/plain"),
                        ternaFoto.value?.eliminarFoto.toString()
                    )
            }

            //guardando en server
            val res = Api.retrofitService.actualizarUsuario(
                rbNombres,
                rbApePat,
                rbApeMat,
                rbRazSoc,
                rbIdTipoDoc,
                rbNroDoc,
                rbNroCel1,
                rbNroCel2,
                rbAcercaDe,
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
        sesionData.clearLastSincroUsuarios()
        sesionData.clearLastSincroParametros()
        sesionData.clearLastSincroAnuncios()
        sesionData.clearLastSincroServicios()
        sesionData.clearLastSincroDirecciones()
        sesionData.clearLastSincroHorarioConfigs()
        sesionData.clearLastSincroVehiculos()
        _sesionStatus.value = SesionStatus.CLOSED
    }

    fun setSelectedTipoDoc(selectedTipoDoc: TipoDocumento) {
        _selectedTipoDoc.value = selectedTipoDoc
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        _errMsg.value = exceptionError.message
        _status.value = EditStatus.ERROR
    }

    fun nuevaFoto(uri: Uri?, path: String?) {
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

    fun mostrarEliminarFoto(): Boolean {
        val editFoto = ternaFoto.value
        if (editFoto?.eliminarFoto!!)
            return false
        if ((editFoto.urlOriginal ?: "").isNotEmpty() || editFoto.uriFile != null) {
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
}

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

class EstrEditFoto(
    var urlOriginal: String? = null,
    var uriFile: Uri? = null,
    var filePath: String? = null,
    var eliminarFoto: Boolean = false
)