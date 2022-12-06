package pe.com.valegrei.carwashapp.ui.users

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.database.usuario.TipoDocumento
import pe.com.valegrei.carwashapp.database.usuario.TipoUsuario
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.database.usuario.UsuarioDao
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR, NORMAL }

class UsersViewModel(
    private val sesionData: SesionData, private val usuarioDao: UsuarioDao
) : ViewModel() {
    private val TAG = UsersViewModel::class.simpleName

    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    private var _selectedUsu = MutableLiveData<Usuario>()
    val selectedUsu: LiveData<Usuario> = _selectedUsu

    fun setSelectedUsu(usuario: Usuario){
        _selectedUsu.value = usuario.copy()
    }

    fun cambiarTipoUsuario(idtipoUsuario: Int) {
        val usuario = selectedUsu.value
        usuario?.idTipoUsuario = idtipoUsuario
        when(idtipoUsuario){
            TipoUsuario.DISTR.id -> usuario?.idTipoDocumento = TipoDocumento.RUC.id
            else -> usuario?.idTipoDocumento = TipoDocumento.DNI.id
        }
        usuario?.nroDocumento = ""
        _selectedUsu.value = usuario!!
    }

    fun cambiarEstadoUsuario() {
        val usuario = selectedUsu.value
        usuario?.estado = !usuario?.estado!!
        _selectedUsu.value = usuario!!
    }

    fun cargarUsuarios(): Flow<List<Usuario>> = usuarioDao.obtenerUsuarios()

    fun descargarUsuarios() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroUsuarios()
            descargarUsuarios(sesion, lastSincro)
            _status.value = Status.SUCCESS
        }
    }

    private suspend fun descargarUsuarios(sesion: Sesion?, lastSincro: Date) {
        val res = Api.retrofitService.obtenerUsuarios(lastSincro, sesion?.getTokenBearer()!!)
        val usuarios = res.data.usuarios
        if (usuarios.isNotEmpty()) {
            usuarioDao.guardarUsuarios(usuarios)
        }
        sesionData.saveLastSincroUsuarios(res.timeStamp)
    }

    fun guardarCambios() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroUsuarios()
            val usuario = selectedUsu.value
            //guarda lo cambiado
            Api.retrofitService.modificarUsuario(usuario?.id!!, usuario, sesion?.getTokenBearer()!!)
            //Trae los cambios
            descargarUsuarios(sesion, lastSincro)
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