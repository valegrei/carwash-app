package pe.com.carwashperuapp.carwashapp.ui.reserve_list_dis

import android.util.Log
import androidx.lifecycle.*
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.model.Reserva
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import pe.com.carwashperuapp.carwashapp.ui.util.formatoFechaDB
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR }
enum class EditStatus { EXIT, VIEW }
class ReserveListViewModel(
    private val sesionData: SesionData,
) : ViewModel() {
    private val TAG = ReserveListViewModel::class.simpleName

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

    fun verReserva(reserva: Reserva) {
        _errMsg.value = ""
        _selectedReserva.value = reserva
        _editStatus.value = EditStatus.VIEW
        _mostrarEditar.value = true
    }

    fun consultarReservas() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val time = MaterialDatePicker.todayInUtcMilliseconds()
            val fecha = formatoFechaDB(time)
            try {
                val res = Api.retrofitService.obtenerReservasDistrib(
                    null,
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

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        _errMsg.value = exceptionError.message
        _status.value = Status.ERROR
    }
}

class ReserveListViewModelFactory(
    private val sesionData: SesionData,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReserveListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReserveListViewModel(sesionData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}