package pe.com.carwashperuapp.carwashapp.ui.reserves_list_cli

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.model.Favorito
import pe.com.carwashperuapp.carwashapp.model.Reserva
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import pe.com.carwashperuapp.carwashapp.network.request.ReqFavorito
import pe.com.carwashperuapp.carwashapp.ui.util.formatoFechaDB
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR }
enum class EditStatus { EXIT, VIEW }
class MyReserveViewModel(
    private val sesionData: SesionData,
) : ViewModel() {
    private val TAG = MyReserveViewModel::class.simpleName

    //errores
    private var _errMsg = MutableLiveData<String?>()
    val errMsg: LiveData<String?> = _errMsg

    //estados
    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status
    private var _editStatus = MutableLiveData<EditStatus>()
    val editStatus: LiveData<EditStatus> = _editStatus
    private var _mostrarEditar = MutableLiveData<Boolean>()
    val mostrarEditar: LiveData<Boolean> = _mostrarEditar

    //seleccionados
    private var _reservas = MutableLiveData<List<Reserva>>()
    val reservas: LiveData<List<Reserva>> = _reservas
    private var _selectedReserva = MutableLiveData<Reserva>()
    val selectedReserva: LiveData<Reserva> = _selectedReserva
    private var _selectedFecha = MutableLiveData<Long?>()
    val selectedFecha: LiveData<Long?> = _selectedFecha
    private var _favorito = MutableLiveData<Favorito?>()
    val favorito: LiveData<Favorito?> = _favorito
    private var _mostrarFavorito = MutableLiveData<Boolean>()
    val mostrarFavorito: LiveData<Boolean> = _mostrarFavorito

    fun verReserva(reserva: Reserva) {
        _errMsg.value = ""
        _selectedReserva.value = reserva
        cargarFavorito()
        _editStatus.value = EditStatus.VIEW
        _mostrarEditar.value = true
    }

    init {
        limpiarFecha()
    }
    fun seleccionrFecha(time: Long) {
        _selectedFecha.value = time
    }

    fun limpiarFecha() {
        _selectedFecha.value = null
    }

    fun consultarReservas() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val time = selectedFecha.value
            var fecha: String? = null
            if (time != null)
                fecha = formatoFechaDB(time)
            try {
                val res = Api.retrofitService.obtenerReservas(
                    fecha,
                    sesion?.getTokenBearer()!!,
                )
                _reservas.value = res.data.reservas
            } catch (e: Exception) {
                _reservas.value = listOf()
            }
            _status.value = Status.SUCCESS
        }
    }

    private fun clearErrs() {
        _errMsg.value = null
    }

    fun anularReserva() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val idReserva = selectedReserva.value?.id!!
            Api.retrofitService.eliminarReserva(idReserva, sesion?.getTokenBearer()!!)
            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }
    fun mostrarLlamar(): Boolean {
        return (selectedReserva.value?.distrib?.nroCel1 ?: "").isNotEmpty()
    }

    private fun cargarFavorito() {
        val favs = selectedReserva.value?.local?.favoritos
        if (favs.isNullOrEmpty()) {
            _favorito.value = null
            _mostrarFavorito.value = false
        } else {
            _favorito.value = favs[0]
            _mostrarFavorito.value = true
        }
    }

    fun marcarFavorito() {
        _mostrarFavorito.value = true
    }

    fun desmarcarFavorito() {
        _mostrarFavorito.value = false
    }
    fun guardarFavorito() {
        if (mostrarFavorito.value!!) {
            // revisar si antes no estaba marcado
            if (favorito.value == null) agregarFavorito()
        } else {
            // revisar si antes estaba marcado
            if (favorito.value != null) eliminarFavorito()
        }
    }

    private fun eliminarFavorito() {
        viewModelScope.launch(exceptionHandler) {
            val sesion = sesionData.getCurrentSesion()
            try {
                Api.retrofitService.eliminarFavorito(
                    favorito.value?.id!!,
                    sesion?.getTokenBearer()!!,
                )
                _favorito.value = null
                _mostrarFavorito.value = false
            } catch (_: Exception) {
                _mostrarFavorito.value = true
            }
        }
    }

    private fun agregarFavorito() {
        viewModelScope.launch(exceptionHandler) {
            val sesion = sesionData.getCurrentSesion()
            try {
                val res = Api.retrofitService.agregarFavorito(
                    ReqFavorito(selectedReserva.value?.local?.id!!),
                    sesion?.getTokenBearer()!!,
                )
                _favorito.value = res.data.favorito
                _mostrarFavorito.value = true
            } catch (e: Exception) {
                _favorito.value = null
                _mostrarFavorito.value = false
            }
        }
    }


    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        _errMsg.value = exceptionError.message
        _status.value = Status.ERROR
    }
}

class MyReserveViewModelFactory(
    private val sesionData: SesionData,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyReserveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyReserveViewModel(sesionData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}