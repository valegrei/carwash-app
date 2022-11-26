package pe.com.valegrei.carwashapp.ui.my_places

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pe.com.valegrei.carwashapp.databinding.ItemMyPlacesBinding
import pe.com.valegrei.carwashapp.model.Local

class MyPlacesListAdapter(
    private val dataSet: Array<Local>,
    private var listener: OnInteractionListener
) :
    RecyclerView.Adapter<MyPlacesListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val binding: ItemMyPlacesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Local, listener: OnInteractionListener) {
            binding.apply {
                tvDepartamento.text = data.departamento
                tvProvincia.text = data.provincia
                tvDireccion.text = data.direccion
                cardview.setOnClickListener { listener.onClick(data) }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMyPlacesBinding =
            ItemMyPlacesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.bindData(dataSet[position], listener)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    interface OnInteractionListener {
        fun onClick(item: Local)
    }
}