package pe.com.valegrei.carwashapp.ui.announcement

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.anuncio.Anuncio
import pe.com.valegrei.carwashapp.database.anuncio.AnuncioDao
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR, NORMAL }
class AnnouncementViewModel(
    private val sesionData: SesionData, private val anuncioDao: AnuncioDao
) : ViewModel() {
    private val TAG = AnnouncementViewModel::class.simpleName

    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status


    fun cargarAnuncios(): Flow<List<Anuncio>> = anuncioDao.obtenerAnuncios()

    fun descargarAnuncios() {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroAnuncios()
            descargarAnuncios(sesion, lastSincro)
            _status.value = Status.SUCCESS
        }
    }

    private suspend fun descargarAnuncios(sesion: Sesion?, lastSincro: Date) {
        val res = Api.retrofitService.obtenerAnuncios(lastSincro, sesion?.getTokenBearer()!!)
        val anuncios = res.getAnunciosDb()
        if (anuncios.isNotEmpty()) {
            anuncioDao.guardarAnuncios(anuncios)
        }
        sesionData.saveLastSincroAnuncios(res.timeStamp)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, e.message, e)
        val exceptionError = e.handleThrowable()
        _errMsg.value = exceptionError.message
        _status.value = Status.ERROR
    }
}

class AnnouncementViewModelFactory(
    private val sesionData: SesionData, private val anuncioDao: AnuncioDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnnouncementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnnouncementViewModel(sesionData, anuncioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}