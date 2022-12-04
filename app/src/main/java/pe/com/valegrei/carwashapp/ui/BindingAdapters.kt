package pe.com.valegrei.carwashapp.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import pe.com.valegrei.carwashapp.R

@BindingAdapter("checkStatus")
fun bindCheckStatus(checkImageView: ImageView, checked: Boolean) {
    if (checked) {
        checkImageView.setImageResource(R.drawable.ic_check_circle_green_24)
    } else {
        checkImageView.setImageResource(R.drawable.ic_check_circle_grey_24)
    }
}