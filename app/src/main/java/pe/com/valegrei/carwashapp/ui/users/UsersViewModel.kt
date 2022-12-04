package pe.com.valegrei.carwashapp.ui.users

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.database.usuario.UsuarioDao
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import pe.com.valegrei.carwashapp.ui.distribs.DistribsViewModel
import pe.com.valegrei.carwashapp.ui.distribs.Status

enum class Status { LOADING, SUCCESS, ERROR, NORMAL }

class UsersViewModel(
    private val sesionData: SesionData, private val usuarioDao: UsuarioDao
) : ViewModel() {
    private val TAG = DistribsViewModel::class.simpleName

    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    fun cargarUsuarios(): Flow<List<Usuario>> = usuarioDao.obtenerUsuarios()

    fun descargarUsuarios() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroUsuarios()
            val res = Api.retrofitService.obtenerUsuarios(lastSincro, sesion?.getTokenBearer()!!)
            val usuarios = res.data.usuarios
            if (usuarios.isNotEmpty()) {
                usuarioDao.guardarUsuarios(usuarios)
            }
            sesionData.saveLastSincroUsuarios(res.timeStamp)
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

class UsersViewModelFactory(
    private val sesionData: SesionData, private val usuarioDao: UsuarioDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsersViewModel(sesionData, usuarioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}