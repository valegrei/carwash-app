package pe.com.valegrei.carwashapp.ui.reserve_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.com.valegrei.carwashapp.model.Reserva

class ReserveListViewModel : ViewModel() {

    private val _reserveList = MutableLiveData<Array<Reserva>>().apply {
        value = arrayOf()//Reserva.dataSet
    }
    val reserveList: LiveData<Array<Reserva>> = _reserveList
}