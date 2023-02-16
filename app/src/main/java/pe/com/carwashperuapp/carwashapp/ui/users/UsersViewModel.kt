package pe.com.carwashperuapp.carwashapp.ui.users

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.sesion.Sesion
import pe.com.carwashperuapp.carwashapp.database.usuario.*
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import pe.com.carwashperuapp.carwashapp.network.request.ReqCambiarClaveAdmin
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR }
enum class GoStatus { SHOW_DELETE, SHOW_CHANGE_PASSWORD, NORMAL }

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

    private var _goStatus = MutableLiveData<GoStatus>()
    val goStatus: LiveData<GoStatus> = _goStatus

    private var _showAdmin = MutableLiveData<Boolean>()
    val showAdmin: LiveData<Boolean> = _showAdmin

    private var _showCli = MutableLiveData<Boolean>()
    val showCli: LiveData<Boolean> = _showCli

    private var _showDis = MutableLiveData<Boolean>()
    val showDis: LiveData<Boolean> = _showDis

    init {
        _showAdmin.value = true
        _showCli.value = true
        _showDis.value = true
    }

    fun setSelectedUsu(usuario: Usuario) {
        _selectedUsu.value = usuario.copy()
    }

    fun showEliminar() {
        _goStatus.value = GoStatus.SHOW_DELETE
    }

    fun showCambiarClave() {
        clearClave()
        _goStatus.value = GoStatus.SHOW_CHANGE_PASSWORD
    }

    fun clearGoStatus() {
        _goStatus.value = GoStatus.NORMAL
    }

    fun mostrarPendienteVerif(): Boolean {
        if (selectedUsu.value?.estado!! == EstadoUsuario.VERIFICANDO.id) return true
        return false
    }

    fun mostrarEliminar(): Boolean {
        val correoActual = sesionData.getCurrentSesion()?.usuario?.correo ?: ""
        if (selectedUsu.value?.correo == correoActual) return false
        return true
    }

    fun cargarUsuarios(): Flow<List<Usuario>> {
        val lista = mutableListOf<Int>()
        if(showAdmin.value!!) lista.add(TipoUsuario.ADMIN.id)
        if(showCli.value!!) lista.add(TipoUsuario.CLIENTE.id)
        if(showDis.value!!) lista.add(TipoUsuario.DISTR.id)
        return usuarioDao.obtenerUsuarios(lista)
    }

    fun descargarUsuarios() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroUsuarios()
            descargarUsuarios(sesion, lastSincro)
            _status.value = Status.SUCCESS
        }
    }

    private suspend fun descargarUsuarios(sesion: Sesion?, lastSincro: String?) {
        val res = Api.retrofitService.obtenerUsuarios(lastSincro, sesion?.getTokenBearer()!!)
        val usuarios = res.data.usuarios
        if (usuarios.isNotEmpty()) {
            usuarioDao.guardarUsuarios(usuarios)
        }
        sesionData.saveLastSincroUsuarios(res.timeStamp)
    }

    fun eliminarUsuario() {
        val usuario = selectedUsu.value
        usuario?.estado = EstadoUsuario.INACTIVO.id
        _selectedUsu.value = usuario!!
        guardarCambios()
    }

    private fun guardarCambios() {
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

    //Datos para editar cuenta
    var claveNew = MutableLiveData<String>()
    var claveNewRe = MutableLiveData<String>()

    fun cambiarClave() {
        val claveNew = (this.claveNew.value ?: "").trim()
        val claveNewRe = (this.claveNewRe.value ?: "").trim()
        if (validar(claveNew, claveNewRe)) {
            cambiarClave(claveNew)
        }
    }

    private fun validar(claveNew: String, claveNewRe: String): Boolean {
        if (claveNew.isEmpty()) {
            _errMsg.value = "Campo nueva clave vacío"
            return false
        }
        if (claveNewRe.isEmpty()) {
            _errMsg.value = "Campo repetir nueva clave vacío"
            return false
        }
        if (claveNew != claveNewRe) {
            _errMsg.value = "Claves no coindicen"
            return false
        }
        return true
    }

    private fun clearClave() {
        claveNew.value = ""
        claveNewRe.value = ""
    }

    private fun cambiarClave(claveNew: String) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            //guarda lo cambiado
            Api.retrofitService.cambiarClave(
                selectedUsu.value?.id!!,
                ReqCambiarClaveAdmin(claveNew),
                sesion?.getTokenBearer()!!
            )
            _status.value = Status.SUCCESS
        }
    }

    fun setFiltros(checkedItems: BooleanArray) {
        _showAdmin.value = checkedItems[0]
        _showCli.value = checkedItems[1]
        _showDis.value = checkedItems[2]
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        if (!exceptionError.message.contains("No hay")) {
            _errMsg.value = exceptionError.message
        }
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