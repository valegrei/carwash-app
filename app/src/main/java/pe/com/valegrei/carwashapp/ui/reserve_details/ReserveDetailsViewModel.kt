package pe.com.valegrei.carwashapp.ui.reserve_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.com.valegrei.carwashapp.model.Reserva
import pe.com.valegrei.carwashapp.model.Servicio

class ReserveDetailsViewModel : ViewModel() {
    private val _reserveDetails = MutableLiveData<Array<Servicio>>().apply {
        value = Servicio.dataSet
    }
    val reserveDetails: LiveData<Array<Servicio>> = _reserveDetails

    private val _reserveItem = MutableLiveData<Reserva>().apply {
        value = Reserva.dataSet[0]
    }

    val reservaItems: LiveData<Reserva> = _reserveItem
}