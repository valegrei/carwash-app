package pe.com.carwashperuapp.carwashapp.ui.reserve_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.com.carwashperuapp.carwashapp.model.Reserva1
import pe.com.carwashperuapp.carwashapp.model.Servicio

class ReserveDetailsViewModel : ViewModel() {
    private val _reserveDetails = MutableLiveData<Array<Servicio>>().apply {
        value = Servicio.dataSet
    }
    val reserveDetails: LiveData<Array<Servicio>> = _reserveDetails

    private val _reserveItem = MutableLiveData<Reserva1>().apply {
        value = Reserva1.dataSet[0]
    }

    val reservaItems: LiveData<Reserva1> = _reserveItem
}