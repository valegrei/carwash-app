package pe.com.valegrei.carwashapp.ui.new_password

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.database.usuario.EstadoUsuario
import pe.com.valegrei.carwashapp.database.usuario.TipoUsuario
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import pe.com.valegrei.carwashapp.network.request.ReqCambiarClave
import java.util.*


enum class Status { LOADING, ERROR, GO_ADMIN, GO_CLIENT, GO_DISTR, VERIFICAR, CLEARED }

class NewPasswordViewModel(private val sesionData: SesionData) :
    ViewModel() {

    private val TAG = NewPasswordViewModel::class.simpleName
    private var _correo = MutableLiveData<String>()
    val correo: LiveData<String> = _correo
    var codigo = MutableLiveData<String>()
    var clave = MutableLiveData<String>()
    var repClave = MutableLiveData<String>()
    private var _errCodigo = MutableLiveData<String>()
    val errCodigo: LiveData<String> = _errCodigo
    private var _errClave = MutableLiveData<String>()
    val errClave: LiveData<String> = _errClave
    private var _errRepClave = MutableLiveData<String>()
    val errRepClave: LiveData<String> = _errRepClave
    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg
    private var _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    init {
        codigo.value = ""
        clave.value = ""
        repClave.value = ""
    }

    fun cargarDatos(correo: String) {
        _correo.value = correo
    }

    private fun guardarSesionUsuario(usuario: Usuario, exp: Date, jwt: String) =
        sesionData.saveSesion(Sesion(exp, jwt, usuario, true))

    private fun validar(codigo: String, clave: String, repClave: String): Boolean {
        var res = true

        _errCodigo.value = ""
        _errClave.value = ""
        _errRepClave.value = ""
        _errMsg.value = ""

        if (codigo.isEmpty()) {
            _errCodigo.value = "Código vacío"
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
        return res
    }

    fun clear() {
        /*codigo.value = ""
        clave.value = ""
        repClave.value = ""*/
        _status.value = Status.CLEARED
    }

    fun renovarClave() {
        val correo = correo.value!!
        val codigo = codigo.value?.trim()!!
        val clave = clave.value?.trim()!!
        val repClave = repClave.value?.trim()!!
        if (validar(codigo, clave, repClave)) {
            renovarClave(correo, codigo, clave)
        }
    }

    private fun renovarClave(correo: String, codigo: String, clave: String) {
        viewModelScope.launch(exceptionHandler) {
            _errMsg.value = ""
            _status.value = Status.LOADING
            val resp = Api.retrofitService.cambiarClave(
                ReqCambiarClave(
                    correo, clave, codigo.toInt()
                )
            )
            //Comprueba si se debe verificar
            if (resp.data.usuario.estado == EstadoUsuario.ACTIVO.id) {
                //procede a guardar
                guardarSesionUsuario(resp.data.usuario, resp.data.exp!!, resp.data.jwt!!)
                verificarSesion(resp.data.usuario)
            } else {
                _usuario.value = resp.data.usuario
                //procede a pasar a verificar correo
                _status.value = Status.VERIFICAR
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

class NewPasswordViewModelFactory(
    private val sesionData: SesionData
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewPasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewPasswordViewModel(sesionData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}