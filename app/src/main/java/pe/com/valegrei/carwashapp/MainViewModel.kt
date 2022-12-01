package pe.com.valegrei.carwashapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.com.valegrei.carwashapp.database.SesionData

enum class SesionStatus { NORMAL, CLOSED}

class MainViewModel(private val sesionData: SesionData) : ViewModel() {
    private var _sesionStatus = MutableLiveData<SesionStatus>()
    val sesionStatus: LiveData<SesionStatus> = _sesionStatus

    init {
        _sesionStatus.value = SesionStatus.NORMAL
    }

    fun cerrarSesion(){
        sesionData.closeSesion()
        _sesionStatus.value = SesionStatus.CLOSED
    }
}

class MainViewModelFactory(
    private val sesionData: SesionData
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(sesionData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}