package pe.com.carwashperuapp.carwashapp.ui.reserve_list_admin

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.model.Reserva
import pe.com.carwashperuapp.carwashapp.model.ReservaEstado
import pe.com.carwashperuapp.carwashapp.model.ServicioEstado
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import pe.com.carwashperuapp.carwashapp.network.request.ReqAtenderReserva
import pe.com.carwashperuapp.carwashapp.ui.util.formatoFechaDB
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR }
enum class EditStatus { EXIT, VIEW }
class ReserveListAdminViewModel(
    private val sesionData: SesionData,
) : ViewModel() {
    private val TAG = ReserveListAdminViewModel::class.simpleName

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
    private var _filtroDis = MutableLiveData<String>()
    val filtroDis: LiveData<String> = _filtroDis
    private var _filtroCli = MutableLiveData<String>()
    val filtroCli: LiveData<String> = _filtroCli

    fun verReserva(reserva: Reserva) {
        _errMsg.value = ""
        _selectedReserva.value = reserva
        _editStatus.value = EditStatus.VIEW
        _mostrarEditar.value = true
    }

    init {
        limpiarFecha()
        _filtroDis.value = ""
        _filtroCli.value = ""
    }

    fun seleccionrFecha(time: Long) {
        _selectedFecha.value = time
    }

    fun limpiarFecha() {
        _selectedFecha.value = null
    }

    fun setFiltroDis(filtroDis: String) {
        _filtroDis.value = filtroDis.trim()
    }

    fun setFiltroCli(filtroCli: String) {
        _filtroCli.value = filtroCli.trim()
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
                val res = Api.retrofitService.obtenerReservasAdmin(
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

    private fun calcularEstadoAtencion(): Int {
        val detalleServs = selectedReserva.value?.servicioReserva!!
        var cantServicios = detalleServs.size
        var cantAtendidos = 0
        var cantAnulados = 0
        var cantNoAtendidos = 0
        detalleServs.forEach {
            cantAtendidos += if (it.detalle?.estado == ServicioEstado.ATENDIDO.id) 1 else 0
            cantAnulados += if (it.detalle?.estado == ServicioEstado.ANULADO.id) 1 else 0
            cantNoAtendidos += if (it.detalle?.estado == ServicioEstado.NO_ATENDIDO.id) 1 else 0
        }
        if (cantNoAtendidos == cantServicios)
            return ReservaEstado.NO_ATENDIDO.id
        if (cantAnulados == cantServicios)
            return ReservaEstado.ANULADO.id
        if (cantAtendidos == cantServicios || (cantAnulados + cantAtendidos) == cantServicios)
            return ReservaEstado.ATENDIDO.id
        return ReservaEstado.ATENDIENDO.id
    }

    fun editarReserva() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val idReserva = selectedReserva.value?.id!!
            val estadoAtencion = calcularEstadoAtencion()
            val reqAtenderReserva = ReqAtenderReserva(
                selectedReserva.value?.servicioReserva!!,
                estadoAtencion
            )
            Api.retrofitService.atenderReservaAdmin(
                idReserva,
                reqAtenderReserva,
                sesion?.getTokenBearer()!!
            )
            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        _errMsg.value = exceptionError.message
        _status.value = Status.ERROR
    }
}

class ReserveListAdminViewModelFactory(
    private val sesionData: SesionData,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReserveListAdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReserveListAdminViewModel(sesionData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}