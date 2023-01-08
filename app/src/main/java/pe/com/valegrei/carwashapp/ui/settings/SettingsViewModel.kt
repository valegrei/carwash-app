package pe.com.valegrei.carwashapp.ui.settings

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.parametro.*
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import pe.com.valegrei.carwashapp.network.request.ReqParamsCorreo
import pe.com.valegrei.carwashapp.network.request.ReqParamsSMTP
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR, LOADING_BLOCK, NORMAL }

class SettingsViewModel(
    val sesionData: SesionData, val parametroDao: ParametroDao
) : ViewModel() {
    private val TAG = SettingsViewModel::class.simpleName
    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg
    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    private var _parametros = MutableLiveData<MutableMap<String, String>>()
    val parametros: LiveData<MutableMap<String, String>> = _parametros

    init {
        _parametros.value = mutableMapOf()
    }

    fun setParametros(parametros: Map<String, String>) {
        _parametros.value = parametros.toMutableMap()
    }

    fun cargarParametros(): Flow<Map<String, String>> = parametroDao.obtenerParametrosServer()

    fun setHost(host: String) {
        val params = parametros.value
        params?.put(EMAIL_HOST, host)
        _parametros.value = params!!
    }

    fun setPort(port: String) {
        val params = parametros.value
        params?.put(EMAIL_PORT, port)
        _parametros.value = params!!
    }

    fun setSecure(secure: Int) {
        val params = parametros.value
        params?.put(EMAIL_SSL_TLS, secure.toString())
        _parametros.value = params!!
    }

    fun setAddress(address: String) {
        val params = parametros.value
        params?.put(EMAIL_ADDR, address)
        _parametros.value = params!!
    }

    fun setPass(pass: String) {
        val params = parametros.value
        params?.put(EMAIL_PASS, pass)
        _parametros.value = params!!
    }

    fun verificarGuardarSMTP() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING_BLOCK
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroParametros()
            val host = parametros.value?.get(EMAIL_HOST) ?: ""
            val port = parametros.value?.get(EMAIL_PORT) ?: ""
            val secure = parametros.value?.get(EMAIL_SSL_TLS) ?: ""

            //guardando en server
            Api.retrofitService.guardarVerificarSMTP(
                ReqParamsSMTP(host, port, secure),
                sesion?.getTokenBearer()!!
            )
            descargarParams(sesion, lastSincro)
            _status.value = Status.SUCCESS
        }
    }

    fun descargarParams() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroParametros()
            descargarParams(sesion, lastSincro)
            _status.value = Status.SUCCESS
        }
    }

    private suspend fun descargarParams(sesion: Sesion?, lastSincro: Date) {
        val res = Api.retrofitService.obtenerParametros(lastSincro, sesion?.getTokenBearer()!!)
        val parametros = res.data.parametros
        if (parametros.isNotEmpty()) {
            parametroDao.guardarParametros(parametros)
        }
        sesionData.saveLastSincroParametros(res.timeStamp)
    }

    fun guardarCorreo() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING_BLOCK
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroParametros()
            val correo = parametros.value?.get(EMAIL_ADDR) ?: ""
            val pass = parametros.value?.get(EMAIL_PASS) ?: ""

            //guardando en server
            Api.retrofitService.guardarCorreo(
                ReqParamsCorreo(correo, pass),
                sesion?.getTokenBearer()!!
            )
            descargarParams(sesion, lastSincro)
            _status.value = Status.SUCCESS
        }
    }

    fun probarCorreo() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING_BLOCK
            val sesion = sesionData.getCurrentSesion()

            //guardando en server
            Api.retrofitService.probarCorreo(
                sesion?.getTokenBearer()!!
            )
            _status.value = Status.SUCCESS
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        if (exceptionError.message.contains("Revisar")) {
            _errMsg.value = "Error al verificar. Revisar parámetros."
            _status.value = Status.ERROR
        } else if (exceptionError.message.contains("No hay")) {
            _status.value = Status.SUCCESS
        } else {
            _errMsg.value = exceptionError.message
            _status.value = Status.ERROR
        }
    }
}

class SettingsViewModelFactory(
    private val sesionData: SesionData,
    private val parametroDao: ParametroDao,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(sesionData, parametroDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}