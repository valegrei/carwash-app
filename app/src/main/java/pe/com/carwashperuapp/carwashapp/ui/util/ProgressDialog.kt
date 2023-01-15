package pe.com.carwashperuapp.carwashapp.ui.util

import androidx.fragment.app.DialogFragment
import pe.com.carwashperuapp.carwashapp.R

class ProgressDialog : DialogFragment(R.layout.dialog_progress) {
    init {
        isCancelable = false
    }
}