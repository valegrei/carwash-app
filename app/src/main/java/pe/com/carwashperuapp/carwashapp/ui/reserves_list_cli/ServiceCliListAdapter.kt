package pe.com.carwashperuapp.carwashapp.ui.reserves_list_cli

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pe.com.carwashperuapp.carwashapp.databinding.ItemServiceReserveDetailCliBinding
import pe.com.carwashperuapp.carwashapp.model.ServicioReserva

class ServiceCliListAdapter :
    ListAdapter<ServicioReserva, ServiceCliListAdapter.ViewHolder>(DiffCallback) {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val binding: ItemServiceReserveDetailCliBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(servicio: ServicioReserva) {
            binding.apply {
                binding.servicio = servicio
                binding.executePendingBindings()
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemServiceReserveDetailCliBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.bind(getItem(holder.adapterPosition))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ServicioReserva>() {
        override fun areItemsTheSame(oldItem: ServicioReserva, newItem: ServicioReserva): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ServicioReserva,
            newItem: ServicioReserva
        ): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.nombre == newItem.nombre
                    && oldItem.detalle?.precio == newItem.detalle?.precio
                    && oldItem.detalle?.duracion == newItem.detalle?.duracion
                    && oldItem.detalle?.estado == newItem.detalle?.estado
        }

    }
}