package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.vehiculo.Vehiculo
import pe.com.carwashperuapp.carwashapp.databinding.ItemCarDialogBinding

class VehiculoAdapter(
    context: Context,
    objects: List<Vehiculo>,
    private val resource: Int = R.layout.item_car_dialog
) :
    ArrayAdapter<Vehiculo>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemCarDialogBinding = if (convertView != null) {
            DataBindingUtil.getBinding(convertView)!!
        } else {
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                resource,
                parent,
                false
            )
        }
        binding.vehiculo = getItem(position)
        return binding.root
    }

}