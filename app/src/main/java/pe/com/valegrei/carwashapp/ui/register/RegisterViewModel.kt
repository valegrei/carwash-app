package pe.com.valegrei.carwashapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    private var _isDistrib = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isDistrib: LiveData<Boolean> = _isDistrib

    fun setIsDistrib(boolean: Boolean) {
        _isDistrib.value = boolean
    }
}