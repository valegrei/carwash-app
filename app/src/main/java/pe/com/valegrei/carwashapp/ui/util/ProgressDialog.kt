package pe.com.valegrei.carwashapp.ui.util

import androidx.fragment.app.DialogFragment
import pe.com.valegrei.carwashapp.R

class ProgressDialog : DialogFragment(R.layout.dialog_progress) {
    init {
        isCancelable = false
    }
}