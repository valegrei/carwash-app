package pe.com.carwashperuapp.carwashapp.ui.register

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.database.usuario.TipoDocumento
import pe.com.carwashperuapp.carwashapp.database.usuario.TipoUsuario
import pe.com.carwashperuapp.carwashapp.database.usuario.Usuario
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable

enum class Status { LOADING, ERROR, GO_VERIFY, CLEARED, GO_LOGIN }

class RegisterViewModel : ViewModel() {
    private val TAG = RegisterViewModel::class.simpleName
    private var _isDistrib = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isDistrib: LiveData<Boolean> = _isDistrib

    fun setIsDistrib(boolean: Boolean) {
        _isDistrib.value = boolean
    }

    var correo = MutableLiveData<String>()
    var clave = MutableLiveData<String>()
    var repClave = MutableLiveData<String>()
    var razSoc = MutableLiveData<String>()
    var nroDoc = MutableLiveData<String>()
    var nroCel1 = MutableLiveData<String>()
    var nroCel2 = MutableLiveData<String>()
    private var _errCorreo = MutableLiveData<String>()
    val errCorreo: LiveData<String> = _errCorreo
    private var _errClave = MutableLiveData<String>()
    val errClave: LiveData<String> = _errClave
    private var _errRepClave = MutableLiveData<String>()
    val errRepClave: LiveData<String> = _errRepClave
    private var _errRazSoc = MutableLiveData<String>()
    val errRazSoc: LiveData<String> = _errRazSoc
    private var _errNroDoc = MutableLiveData<String>()
    val errNroDoc: LiveData<String> = _errNroDoc
    private var _errNroCel1 = MutableLiveData<String>()
    val errNroCel1: LiveData<String> = _errNroCel1
    private var _errNroCel2 = MutableLiveData<String>()
    val errNroCel2: LiveData<String> = _errNroCel2
    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg
    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status
    private var _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario

    init {
        correo.value = ""
        clave.value = ""
        repClave.value = ""
        _isDistrib.value = false
        razSoc.value = ""
        nroDoc.value = ""
        nroCel1.value = ""
        nroCel2.value = ""
        _errCorreo.value = ""
        _errClave.value = ""
        _errRepClave.value = ""
        _errRazSoc.value = ""
        _errNroDoc.value = ""
        _errNroCel1.value = ""
        _errNroCel2.value = ""
        _errMsg.value = ""
    }

    fun clear(){
        /*correo.value = ""
        clave.value = ""
        repClave.value = ""
        _isDistrib.value = false
        razSoc.value = ""
        nroDoc.value = ""
        nroCel1.value = ""
        nroCel2.value = ""
        _errCorreo.value = ""
        _errClave.value = ""
        _errRepClave.value = ""
        _errRazSoc.value = ""
        _errNroDoc.value = ""
        _errNroCel1.value = ""
        _errNroCel2.value = ""
        _errMsg.value = ""*/
        _status.value = Status.CLEARED
    }

    private fun validar(
        correo: String,
        clave: String,
        repClave: String,
        isDistrib: Boolean,
        razSoc: String,
        nroDoc: String,
        nroCel1: String,
        nroCel2: String
    ): Boolean {
        var res = true
        //limpia errores
        _errCorreo.value = ""
        _errClave.value = ""
        _errRepClave.value = ""
        _errRazSoc.value = ""
        _errNroDoc.value = ""
        _errNroCel1.value = ""
        _errNroCel2.value = ""
        _errMsg.value = ""
        if (correo.isEmpty()) {
            _errCorreo.value = "Campo vacío"
            res = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _errCorreo.value = "Correo inválido"
            res = false
        }
        if (clave.isEmpty()) {
            _errClave.value = "Campo vacío"
            res = false
        }
        if (repClave.isEmpty()) {
            _errRepClave.value = "Campo vacío"
            res = false
        } else if (repClave != clave) {
            _errRepClave.value = "No coinciden"
            res = false
        }

        if (isDistrib) {
            if (razSoc.isEmpty()) {
                _errRazSoc.value = "Campo vacío"
                res = false
            }
            if (nroDoc.isEmpty()) {
                _errNroDoc.value = "Campo vacío"
                res = false
            }/* else if (nroDoc.length != TipoDocumento.RUC.digitos) {//RUC
                _errNroDoc.value = "RUC inválido"
                res = false
            }*/
            if (nroCel1.isNotEmpty() && !Patterns.PHONE.matcher(nroCel1).matches()) {
                _errNroCel1.value = "Nro. inválido"
                res = false
            }
            if (nroCel2.isNotEmpty() && !Patterns.PHONE.matcher(nroCel2).matches()) {
                _errNroCel2.value = "Nro. inválido"
                res = false
            }
        }
        return res
    }

    fun registrar() {
        val correo = correo.value?.trim()!!
        val clave = clave.value?.trim()!!
        val repClave = repClave.value?.trim()!!
        val razSoc = razSoc.value?.trim()!!
        val nroDoc = nroDoc.value?.trim()!!
        val nroCel1 = nroCel1.value?.trim()!!
        val nroCel2 = nroCel2.value?.trim()!!
        val isDistrib = isDistrib.value!!

        if (validar(correo, clave, repClave, isDistrib, razSoc, nroDoc, nroCel1, nroCel2)) {
            registrar(correo, clave, isDistrib, razSoc, nroDoc, nroCel1, nroCel2)
        }
    }

    private fun registrar(
        correo: String,
        clave: String,
        isDistrib: Boolean,
        razSoc: String,
        nroDoc: String,
        nroCel1: String,
        nroCel2: String
    ) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val nuevoUsuario = Usuario()
            nuevoUsuario.correo = correo
            nuevoUsuario.clave = clave
            if (isDistrib) {
                nuevoUsuario.idTipoUsuario = TipoUsuario.DISTR.id
                nuevoUsuario.idTipoDocumento = TipoDocumento.RUC.id
                nuevoUsuario.razonSocial = razSoc
                nuevoUsuario.nroDocumento = nroDoc
                nuevoUsuario.nroCel1 = nroCel1
                nuevoUsuario.nroCel2 = nroCel2
            } else {
                nuevoUsuario.idTipoUsuario = TipoUsuario.CLIENTE.id
                nuevoUsuario.idTipoDocumento = TipoDocumento.DNI.id
            }
            val resp = Api.retrofitService.registrar(nuevoUsuario)
            val usuResp = resp.data.usuario
            if(usuResp.idTipoUsuario == TipoUsuario.DISTR.id){
                _status.value = Status.GO_LOGIN
            }else{
                _usuario.value = resp.data.usuario
                _status.value = Status.GO_VERIFY
            }
        }
    }

    /**
     * Handler de excepciones para coroutines
     */
    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        _errMsg.value = exceptionError.message
        _status.value = Status.ERROR
    }
}