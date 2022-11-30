package pe.com.valegrei.carwashapp.viewmodels

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.database.sesion.SesionDao
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.database.usuario.UsuarioDao
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import pe.com.valegrei.carwashapp.network.request.ReqLogin

enum class Status { LOADING, ERROR, SUCCESS, VERIFICAR }

class LoginViewModel(private val sesionDao: SesionDao, private val usuarioDao: UsuarioDao) :
    ViewModel() {
    private var _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario
    private val TAG = LoginViewModel::class.simpleName

    var correo = MutableLiveData<String>()
    var clave = MutableLiveData<String>()
    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg
    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    init {
        correo.value = ""
        clave.value = ""
        _errMsg.value = ""
    }

    /**
     * Valida campos de Inicio de Sesion
     */
    private fun validarLogin(correo: String, clave: String): Boolean {
        _errMsg.value = ""
        if (correo.isEmpty()) {
            _errMsg.value = "Correo vacío"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _errMsg.value = "Correo inválido"
            return false
        }
        if (clave.isEmpty()) {
            _errMsg.value = "Contraseña vacía"
            return false
        }
        return true
    }

    fun login() {
        val correo = correo.value!!.trim()
        val clave = clave.value!!.trim()
        if (validarLogin(correo, clave)) {
            login(correo, clave)
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

    private fun login(correo: String, clave: String) {
        viewModelScope.launch(exceptionHandler) {
            _errMsg.value = ""
            _status.value = Status.LOADING
            val resp = Api.retrofitService.iniciarSesion(ReqLogin(correo, clave))

            //Comprueba si se debe verificar
            if (resp.data.usuario.verificado) {
                //procede a guardar
                guardarSesionUsuario(resp.data.usuario, resp.data.exp!!, resp.data.jwt!!)
                _status.value = Status.SUCCESS
            } else {
                _usuario.value = resp.data.usuario
                //procede a pasar a verificar correo
                _status.value = Status.VERIFICAR
            }

        }
    }

    fun guardarSesionUsuario(usuario: Usuario, exp: String, jwt: String) {
        usuarioDao.insertUsuario(usuario)
        sesionDao.insertSesion(Sesion(usuario.id!!, exp, jwt, true))
    }
}

class LoginViewModelFactory(
    private val sesionDao: SesionDao, private val usuarioDao: UsuarioDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(sesionDao, usuarioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}