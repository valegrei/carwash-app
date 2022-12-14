package pe.com.valegrei.carwashapp.ui.add_admin

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.database.usuario.UsuarioDao
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import pe.com.valegrei.carwashapp.network.request.ReqAddAdmin
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR, NORMAL }
class AddAdminViewModel(
    private val sesionData: SesionData,
    private val usuarioDao: UsuarioDao,
) : ViewModel() {
    private val TAG = AddAdminViewModel::class.simpleName

    //Datos para editar cuenta
    var correo = MutableLiveData<String>()
    var nombres = MutableLiveData<String>()
    var apePat = MutableLiveData<String>()
    var apeMat = MutableLiveData<String>()

    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg
    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    fun agregarAdmin() {
        val correo = (this.correo.value ?: "").trim()
        val nombres = (this.nombres.value ?: "").trim()
        val apePat = (this.apePat.value ?: "").trim()
        val apeMat = (this.apeMat.value ?: "").trim()
        if (validar(correo)) {
            agregarAdmin(correo, nombres, apePat, apeMat)
        }
    }

    private fun validar(correo: String): Boolean {
        if (correo.isEmpty()) {
            _errMsg.value = "Campo vacío"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _errMsg.value = "Correo inválido"
            return false
        }
        return true
    }

    private suspend fun descargarUsuarios(sesion: Sesion?, lastSincro: Date) {
        val res = Api.retrofitService.obtenerUsuarios(lastSincro, sesion?.getTokenBearer()!!)
        val usuarios = res.data.usuarios
        if (usuarios.isNotEmpty()) {
            usuarioDao.guardarUsuarios(usuarios)
        }
        sesionData.saveLastSincroUsuarios(res.timeStamp)
    }

    private fun agregarAdmin(correo: String, nombres: String, apePat: String, apeMat: String) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroUsuarios()
            val reqAddAdmin = ReqAddAdmin(correo, nombres, apePat, apeMat);
            //guarda lo cambiado
            Api.retrofitService.agregarAdmin(reqAddAdmin, sesion?.getTokenBearer()!!)
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


class AddAdminViewModelFactory(
    private val sesionData: SesionData,
    private val usuarioDao: UsuarioDao,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddAdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddAdminViewModel(sesionData, usuarioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}