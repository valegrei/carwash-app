package pe.com.carwashperuapp.carwashapp.ui.reserve_list_dis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.com.carwashperuapp.carwashapp.model.Reserva

class ReserveListViewModel : ViewModel() {

    private val _reserveList = MutableLiveData<Array<Reserva>>().apply {
        value = arrayOf()//Reserva.dataSet
    }
    val reserveList: LiveData<Array<Reserva>> = _reserveList
}