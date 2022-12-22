package pe.com.valegrei.carwashapp.ui.distribs

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.database.usuario.EstadoUsuario
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.database.usuario.UsuarioDao
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR }
enum class GoStatus { SHOW_CONFIRM, SHOW_DENEG, NORMAL }

class DistribsViewModel(
    private val sesionData: SesionData, private val usuarioDao: UsuarioDao
) : ViewModel() {
    private val TAG = DistribsViewModel::class.simpleName

    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    private var _goStatus = MutableLiveData<GoStatus>()
    val goStatus: LiveData<GoStatus> = _goStatus

    private var _selectedDistrib = MutableLiveData<Usuario>()
    val selectedDistrib : LiveData<Usuario> = _selectedDistrib

    fun setSelectedDistrib(distrib: Usuario){
        _selectedDistrib.value = distrib.copy()
    }

    fun mostrarAprobar(): Boolean {
        if (selectedDistrib.value?.estado!! == EstadoUsuario.VERIFICANDO.id) return true
        return false
    }

    fun showConfirmar() {
        _goStatus.value = GoStatus.SHOW_CONFIRM
    }

    fun showDenegar() {
        _goStatus.value = GoStatus.SHOW_DENEG
    }

    fun clearGoStatus(){
        _goStatus.value = GoStatus.NORMAL
    }

    fun cargarDistribuidores(): Flow<List<Usuario>>  = usuarioDao.obtenerDistribuidores()

    fun descargarDistribuidores() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroUsuarios()
            descargarDistribuidores(sesion, lastSincro)
            _status.value = Status.SUCCESS
        }
    }

    private suspend fun descargarDistribuidores(sesion: Sesion?, lastSincro: Date){
        val res = Api.retrofitService.obtenerUsuarios(lastSincro,sesion?.getTokenBearer()!!)
        val usuarios = res.data.usuarios
        if(usuarios.isNotEmpty()){
            usuarioDao.guardarUsuarios(usuarios)
        }
        sesionData.saveLastSincroUsuarios(res.timeStamp)
    }

    fun aprobarDist(apruebo: Boolean){
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroUsuarios()
            val distrib = selectedDistrib.value!!
            if(apruebo)
                distrib.estado = EstadoUsuario.ACTIVO.id
            else
                distrib.estado = EstadoUsuario.INACTIVO.id
            //guarda lo cambiado
            Api.retrofitService.modificarUsuario(distrib.id!!,distrib, sesion?.getTokenBearer()!!)
            //Trae los cambios
            descargarDistribuidores(sesion, lastSincro)
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

class DistribsViewModelFactory(
    private val sesionData: SesionData, private val usuarioDao: UsuarioDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DistribsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DistribsViewModel(sesionData, usuarioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}