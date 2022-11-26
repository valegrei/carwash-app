package pe.com.valegrei.carwashapp.ui.my_places

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.com.valegrei.carwashapp.model.Local
import pe.com.valegrei.carwashapp.model.Reserva

class MyPlacesViewModel : ViewModel() {

    private val _localList = MutableLiveData<Array<Local>>().apply {
        value = Local.dataSet
    }
    val localList: LiveData<Array<Local>> = _localList
}