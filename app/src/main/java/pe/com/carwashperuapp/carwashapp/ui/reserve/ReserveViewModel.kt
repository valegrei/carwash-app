package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.direccion.Direccion
import pe.com.carwashperuapp.carwashapp.database.direccion.DireccionDao
import pe.com.carwashperuapp.carwashapp.model.Local
import pe.com.carwashperuapp.carwashapp.network.Api
import pe.com.carwashperuapp.carwashapp.network.handleThrowable
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR }
enum class EditStatus { EXIT, EDIT, NEW, VIEW }
enum class GoStatus { GO_ADD, SHOW_DELETE, NORMAL }
class ReserveViewModel(
    private val sesionData: SesionData,
    private val direccionDao: DireccionDao,
) : ViewModel() {
    private val TAG = ReserveViewModel::class.simpleName

    //errores
    private var _errMsg = MutableLiveData<String?>()
    val errMsg: LiveData<String?> = _errMsg

    //estados
    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status
    private var _editStatus = MutableLiveData<EditStatus>()
    val editStatus: LiveData<EditStatus> = _editStatus
    private var _goStatus = MutableLiveData<GoStatus>()
    val goStatus: LiveData<GoStatus> = _goStatus
    private var _mostrarEditar = MutableLiveData<Boolean>()
    val mostrarEditar: LiveData<Boolean> = _mostrarEditar

    //seleccionados
    val direccion = MutableLiveData<String>()
    private var _selectedDireccion = MutableLiveData<Direccion>()
    val selectedDireccion: LiveData<Direccion> = _selectedDireccion
    private var _locales = MutableLiveData<List<Local>>()
    val locales: LiveData<List<Local>> = _locales
    private var _markersData = MutableLiveData<Map<Marker,Local>>()
    val markersData: LiveData<Map<Marker,Local>> = _markersData

    fun nuevaReserva() {
        this.direccion.value = ""
        _errMsg.value = ""
        _editStatus.value = EditStatus.NEW
        _mostrarEditar.value = true
    }

    fun consultarLocales(cornerNE: LatLng, cornerSW: LatLng) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroDirecciones()
            try {
                val res = Api.retrofitService.obtenerLocales(
                    cornerNE.latitude.toString(),
                    cornerNE.longitude.toString(),
                    cornerSW.latitude.toString(),
                    cornerSW.longitude.toString(),
                    sesion?.getTokenBearer()!!,
                )
                _locales.value = res.data.locales
            } catch (e: Exception) {
                _locales.value = listOf()
            }
            _status.value = Status.SUCCESS
        }
    }

    fun setMarkersData(map: Map<Marker, Local>){
        _markersData.value = map
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

class ReserveViewModelFactory(
    private val sesionData: SesionData,
    private val direccionDao: DireccionDao,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReserveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReserveViewModel(sesionData, direccionDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}