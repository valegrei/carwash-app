package pe.com.valegrei.carwashapp.ui.verify

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.database.usuario.TipoUsuario
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import pe.com.valegrei.carwashapp.network.request.ReqId
import pe.com.valegrei.carwashapp.network.request.ReqVerificarCorreo
import java.util.*

enum class Status { LOADING, ERROR, GO_ADMIN, GO_CLIENT, GO_DISTR, SENT_CODE, NORMAL, CLEARED, GO_LOGIN }

class VerifyViewModel(private val sesionData: SesionData) :
    ViewModel() {

    private val TAG = VerifyViewModel::class.simpleName
    private var _idUsuario = MutableLiveData<Int>()
    private var _correo = MutableLiveData<String>()
    val idUsuario: LiveData<Int> = _idUsuario
    val correo: LiveData<String> = _correo
    var codigo = MutableLiveData<String>()
    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    init {
        _errMsg.value = ""
        codigo.value = ""
    }

    fun cargarDatos(idUsuario: Int, correo: String) {
        _idUsuario.value = idUsuario
        _correo.value = correo
    }

    fun guardarSesionUsuario(usuario: Usuario, exp: Date, jwt: String) =
        sesionData.saveSesion(Sesion(exp, jwt, usuario, true))

    fun validar(codigo: String): Boolean {
        if (codigo.isEmpty()) {
            _errMsg.value = "Código vacío"
            return false
        }
        return true
    }

    fun clearDialogs() {
        _status.value = Status.NORMAL
    }

    fun clear() {
        //codigo.value = ""
        _status.value = Status.CLEARED
    }

    fun verificar() {
        val codigo = codigo.value?.trim()!!
        if (validar(codigo)) {
            verificar(codigo)
        }
    }

    private fun verificar(codigo: String) {
        viewModelScope.launch(exceptionHandler) {
            _errMsg.value = ""
            _status.value = Status.LOADING
            val resp = Api.retrofitService.verificarCorreo(
                ReqVerificarCorreo(
                    idUsuario.value!!,
                    codigo.toInt()
                )
            )

            if (resp.data.usuario.idTipoUsuario == TipoUsuario.DISTR.id && !resp.data.usuario.distAct) {
                //Usuario distribuidor debe activarse
                _status.value = Status.GO_LOGIN
            } else {
                //procede a guardar
                guardarSesionUsuario(resp.data.usuario, resp.data.exp!!, resp.data.jwt!!)
                verificarSesion(resp.data.usuario)
            }
        }
    }


    fun verificarSesion(usuario: Usuario) {
        when (usuario.idTipoUsuario) {
            TipoUsuario.ADMIN.id -> _status.value = Status.GO_ADMIN
            TipoUsuario.CLIENTE.id -> _status.value = Status.GO_CLIENT
            TipoUsuario.DISTR.id -> _status.value = Status.GO_DISTR
            else -> {}
        }
    }

    fun enviarCodigo() {
        viewModelScope.launch(exceptionHandler) {
            _errMsg.value = ""
            _status.value = Status.LOADING
            Api.retrofitService.solicitarCodigoVerificacion(ReqId(idUsuario.value!!))
            _status.value = Status.SENT_CODE
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


class VerifyViewModelFactory(
    private val sesionData: SesionData
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VerifyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VerifyViewModel(sesionData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}