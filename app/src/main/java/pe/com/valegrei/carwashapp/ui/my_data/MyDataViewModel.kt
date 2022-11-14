package pe.com.valegrei.carwashapp.ui.my_data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.com.valegrei.carwashapp.model.AccountData

class MyDataViewModel : ViewModel() {

    private val _accountData = MutableLiveData<AccountData>().apply {
        value = AccountData.cuenta
    }
    val accountData: LiveData<AccountData> = _accountData
}