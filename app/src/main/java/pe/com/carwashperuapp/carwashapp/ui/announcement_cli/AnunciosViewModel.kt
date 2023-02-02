package pe.com.carwashperuapp.carwashapp.ui.announcement_cli

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.com.carwashperuapp.carwashapp.database.anuncio.Anuncio
import pe.com.carwashperuapp.carwashapp.database.anuncio.AnuncioDao

class AnunciosViewModel(
    private val anuncioDao: AnuncioDao
) : ViewModel() {
    private var _anuncios = MutableLiveData<List<Anuncio>>()
    val anuncios: LiveData<List<Anuncio>> = _anuncios
    private var _visto = MutableLiveData<Boolean>()
    private val visto: LiveData<Boolean> = _visto

    init {
        _visto.value = false
        _anuncios.value = anuncioDao.obtenerAnunciosMostrar().shuffled()
    }

    fun setVisto() {
        _visto.value = true
    }

    fun mostrarAnuncios(): Boolean = anuncios.value?.isNotEmpty()!! && !visto.value!!
}

class AnunciosViewModelFactory(
    private val anuncioDao: AnuncioDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnunciosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnunciosViewModel(anuncioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}