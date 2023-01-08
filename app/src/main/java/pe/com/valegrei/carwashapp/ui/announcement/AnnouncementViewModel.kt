package pe.com.valegrei.carwashapp.ui.announcement

import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.anuncio.Anuncio
import pe.com.valegrei.carwashapp.database.anuncio.AnuncioDao
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.network.Api
import pe.com.valegrei.carwashapp.network.handleThrowable
import pe.com.valegrei.carwashapp.network.request.ReqAnuncioActualizar
import pe.com.valegrei.carwashapp.network.request.ReqAnuncioEliminar
import java.io.File
import java.util.*

enum class Status { LOADING, SUCCESS, ERROR, NORMAL }
enum class EditStatus { EXIT, EDIT, NEW, VIEW }

class AnnouncementViewModel(
    private val sesionData: SesionData, private val anuncioDao: AnuncioDao
) : ViewModel() {
    private val TAG = AnnouncementViewModel::class.simpleName

    private var _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status
    private var _editStatus = MutableLiveData<EditStatus>()
    val editStatus: LiveData<EditStatus> = _editStatus
    private var _mostrarEditar = MutableLiveData<Boolean>()
    val mostrarEditar: LiveData<Boolean> = _mostrarEditar

    var idAnuncio = MutableLiveData<Int>()
    var descripcion = MutableLiveData<String>()
    var url = MutableLiveData<String>()

    private var _imagen = MutableLiveData<TuplaImageEdit>()
    val imagen: LiveData<TuplaImageEdit> = _imagen

    private var _selectedAnuncio = MutableLiveData<Anuncio>()
    val selectedAnuncio: LiveData<Anuncio> = _selectedAnuncio


    fun nuevoAnuncio(uriFile: Uri?, pathFile: String?) {
        _errMsg.value = ""
        descripcion.value = ""
        url.value = ""
        _imagen.value = TuplaImageEdit(null, uriFile, pathFile)
        _editStatus.value = EditStatus.NEW
        _mostrarEditar.value = true
    }

    fun verAnuncio(anuncio: Anuncio) {
        _errMsg.value = ""
        _selectedAnuncio.value = anuncio
        idAnuncio.value = selectedAnuncio.value?.id!!
        _imagen.value = TuplaImageEdit(selectedAnuncio.value?.getUrlArchivo(), null, null)
        _editStatus.value = EditStatus.VIEW
        _mostrarEditar.value = false
    }

    fun editarAnuncio() {
        _errMsg.value = ""
        idAnuncio.value = selectedAnuncio.value?.id!!
        descripcion.value = selectedAnuncio.value?.descripcion!!
        url.value = selectedAnuncio.value?.url!!
        _imagen.value = TuplaImageEdit(selectedAnuncio.value?.getUrlArchivo(), null, null)
        _editStatus.value = EditStatus.EDIT
        _mostrarEditar.value = true
    }

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
        val anuncios = res.data.anuncios
        if (anuncios.isNotEmpty()) {
            anuncioDao.guardarAnuncios(anuncios)
        }
        sesionData.saveLastSincroAnuncios(res.timeStamp)
    }

    private fun validar(url: String): Boolean {
        _errMsg.value = ""
        if (url.isNotEmpty() && !Patterns.WEB_URL.matcher(url).matches()) {
            _errMsg.value = "URL inválido"
            return false
        }
        return true
    }

    fun eliminarAnuncio() {
        val idAnuncio = this.idAnuncio.value ?: 0
        eliminarAnuncio(idAnuncio)
    }

    private fun eliminarAnuncio(idAnuncio: Int) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroAnuncios()

            Api.retrofitService.eliminarAnuncios(
                ReqAnuncioEliminar(listOf(idAnuncio)),
                sesion?.getTokenBearer()!!
            )

            //procede a descargar
            descargarAnuncios(sesion, lastSincro)
            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }

    private fun actualizarAnuncio() {
        val idAnuncio = this.idAnuncio.value ?: 0
        val descr = (descripcion.value ?: "").trim()
        val url = (this.url.value ?: "").trim()
        if (validar(url)) {
            actualizarAnuncio(idAnuncio, descr, url)
        }
    }

    private fun actualizarAnuncio(idAnuncio: Int, descr: String, url: String) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroAnuncios()
            val mostrar = true  //TODO completar logica

            Api.retrofitService.actualizarAnuncio(
                idAnuncio,
                ReqAnuncioActualizar(descr, url, mostrar),
                sesion?.getTokenBearer()!!
            )

            //procede a descargar
            descargarAnuncios(sesion, lastSincro)
            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }


    private fun crearAnuncio() {
        val descr = (descripcion.value ?: "").trim()
        val url = (this.url.value ?: "").trim()
        if (validar(url)) {
            crearAnuncio(descr, url)
        }
    }

    private fun crearAnuncio(descr: String, url: String) {
        viewModelScope.launch(exceptionHandler) {
            _status.value = Status.LOADING
            val sesion = sesionData.getCurrentSesion()
            val lastSincro = sesionData.getLastSincroAnuncios()

            val rbDescr = RequestBody.create(MediaType.parse("text/plain"), descr)
            val rbUrl = RequestBody.create(MediaType.parse("text/plain"), url)

            var rbImagen: MultipartBody.Part? = null
            if (imagen.value?.pathFile != null) {
                val file = File(imagen.value?.pathFile!!)

                if (file.exists())
                    rbImagen = MultipartBody.Part.createFormData(
                        "imagen",
                        file.name,
                        RequestBody.create(MediaType.parse("image/*"), file)
                    )
            }

            Api.retrofitService.crearAnuncio(rbDescr, rbUrl, rbImagen, sesion?.getTokenBearer()!!)

            //procede a descargar
            descargarAnuncios(sesion, lastSincro)
            _status.value = Status.SUCCESS
            _editStatus.value = EditStatus.EXIT
        }
    }

    fun guardarAnuncio() {
        when (editStatus.value) {
            EditStatus.EDIT -> actualizarAnuncio()
            EditStatus.NEW -> crearAnuncio()
            else -> {}
        }
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

class TuplaImageEdit(
    var urlImagen: String?,
    var uriFile: Uri?,
    var pathFile: String?
)