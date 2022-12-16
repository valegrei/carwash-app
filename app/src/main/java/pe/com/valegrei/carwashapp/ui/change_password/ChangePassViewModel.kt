package pe.com.valegrei.carwashapp.ui.change_password

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import pe.com.valegrei.carwashapp.network.request.ReqCambiarClaveUsu
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR, NORMAL }
class ChangePassViewModel(
    private val sesionData: SesionData,
) : ViewModel() {
    private val TAG = ChangePassViewModel::class.simpleName

    //Datos para editar cuenta
    var claveOld = MutableLiveData<String>()
    var claveNew = MutableLiveData<String>()
    var claveNewRe = MutableLiveData<String>()

    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg
    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    fun cambiarClave() {
        val claveOld = (this.claveOld.value ?: "").trim()
        val claveNew = (this.claveNew.value ?: "").trim()
        val claveNewRe = (this.claveNewRe.value ?: "").trim()
        if (validar(claveOld, claveNew, claveNewRe)) {
            cambiarClave(claveOld, claveNew)
        }
    }

    private fun validar(claveOld: String, claveNew: String, claveNewRe: String): Boolean {
        if (claveOld.isEmpty()) {
            _errMsg.value = "Campo clave anterior vacío"
            return false
        }
        if (claveNew.isEmpty()) {
            _errMsg.value = "Campo nueva clave vacío"
            return false
        }
        if (claveNewRe.isEmpty()) {
            _errMsg.value = "Campo repetir nueva clave vacío"
            return false
        }
        if (!claveNew.equals(claveNewRe)) {
            _errMsg.value = "Claves no coindicen"
            return false
        }
        return true
    }

    init {
        claveOld.value = ""
        claveNew.value = ""
        claveNewRe.value = ""
    }

    private fun cambiarClave(claveOld: String, claveNew: String) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            //guarda lo cambiado
            Api.retrofitService.cambiarClaveUsu(
                sesion?.usuario?.id!!,
                ReqCambiarClaveUsu(claveOld, claveNew),
                sesion?.getTokenBearer()!!
            )
            _status.value = Status.SUCCESS
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        _errMsg.value = exceptionError.message
        _status.value = Status.ERROR
    }
}


class ChangePassViewModelFactory(
    private val sesionData: SesionData,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePassViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChangePassViewModel(sesionData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}