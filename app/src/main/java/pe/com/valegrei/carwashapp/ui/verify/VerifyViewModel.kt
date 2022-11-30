package pe.com.valegrei.carwashapp.ui.verify

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.database.sesion.SesionDao
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.database.usuario.UsuarioDao
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import pe.com.valegrei.carwashapp.network.request.ReqId
import pe.com.valegrei.carwashapp.network.request.ReqVerificarCorreo

enum class Status { LOADING, ERROR, GO_MAIN, REQ_CODE }

class VerifyViewModel(private val sesionDao: SesionDao, private val usuarioDao: UsuarioDao) :
    ViewModel() {

    private val TAG = VerifyViewModel::class.simpleName
    var idUsuario = MutableLiveData<Int>()
    var correo = MutableLiveData<String>()
    var codigo = MutableLiveData<String>()
    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    init {
        _errMsg.value = ""
        codigo.value = ""
    }

    fun guardarSesionUsuario(usuario: Usuario, exp: String, jwt: String) {
        usuarioDao.insertUsuario(usuario)
        sesionDao.insertSesion(Sesion(usuario.id!!, exp, jwt, true))
    }

    fun validar(codigo: String): Boolean {
        if (codigo.isEmpty()) {
            _errMsg.value = "Código vacío"
            return false
        }
        return true
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

            //procede a guardar
            guardarSesionUsuario(resp.data.usuario, resp.data.exp!!, resp.data.jwt!!)
            _status.value = Status.GO_MAIN
        }
    }

    fun enviarCodigo() {
        viewModelScope.launch(exceptionHandler) {
            _errMsg.value = ""
            _status.value = Status.LOADING
            val resp = Api.retrofitService.solicitarCodigoVerificacion(
                ReqId(idUsuario.value!!)
            )

            _status.value = Status.REQ_CODE
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
    private val sesionDao: SesionDao, private val usuarioDao: UsuarioDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VerifyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VerifyViewModel(sesionDao, usuarioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}