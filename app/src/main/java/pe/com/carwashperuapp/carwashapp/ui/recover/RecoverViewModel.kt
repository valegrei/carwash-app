package pe.com.carwashperuapp.carwashapp.ui.recover

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import pe.com.carwashperuapp.carwashapp.network.request.ReqCorreo

enum class Status { LOADING, ERROR, SUCCESS, CLEARED }

class RecoverViewModel() :
    ViewModel() {

    private val TAG = RecoverViewModel::class.simpleName
    var correo = MutableLiveData<String>()
    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    init {
        _errMsg.value = ""
        correo.value = ""
    }

    private fun validar(correo: String): Boolean {
        if (correo.isEmpty()) {
            _errMsg.value = "Correo vacío"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _errMsg.value = "Correo inválido"
            return false
        }
        return true
    }

    fun clear() {
        _status.value = Status.CLEARED
    }

    fun solicitarCodigo() {
        val correo = correo.value?.trim()!!
        if (validar(correo)) {
            solicitarCodigo(correo)
        }
    }

    private fun solicitarCodigo(correo: String) {
        viewModelScope.launch(exceptionHandler) {
            _errMsg.value = ""
            _status.value = Status.LOADING
            Api.retrofitService.solicitarCodigoNuevaClave(ReqCorreo(correo))
            _status.value = Status.SUCCESS
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