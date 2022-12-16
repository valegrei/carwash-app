package pe.com.valegrei.carwashapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.usuario.TipoUsuario
import java.util.*

enum class Status { LOGIN, ADMIN, CLIENT, DISTRIB }

class SplashViewModel(private val sesionData: SesionData) : ViewModel() {

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    fun verificarSesion() {
        //verifica antes la version de db
        sesionData.checkDBVersion()
        //verifica datos de sesion
        val sesion = sesionData.getCurrentSesion()
        val fecha = Date().time
        if (sesion == null || !sesion.estado || sesion.fechaExpira.time < fecha) {
            _status.value = Status.LOGIN
            return
        }
        when (sesion.usuario.idTipoUsuario) {
            TipoUsuario.ADMIN.id -> _status.value = Status.ADMIN
            TipoUsuario.CLIENTE.id -> _status.value = Status.CLIENT
            TipoUsuario.DISTR.id -> _status.value = Status.DISTRIB
            else -> {}
        }
    }
}

class SplashViewModelFactory(
    private val sesionData: SesionData
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SplashViewModel(sesionData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}