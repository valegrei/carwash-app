package pe.com.carwashperuapp.carwashapp.ui.my_cars

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pe.com.carwashperuapp.carwashapp.database.vehiculo.Vehiculo
import pe.com.carwashperuapp.carwashapp.databinding.ItemMyCarsBinding

class MyCarsListAdapter(private val onItemClicked: (Vehiculo) -> Unit) :
    ListAdapter<Vehiculo, MyCarsListAdapter.ViewHolder>(DiffCallback) {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val binding: ItemMyCarsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(vehiculo: Vehiculo) {
            binding.apply {
                binding.vehiculo = vehiculo
                binding.executePendingBindings()
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMyCarsBinding =
            ItemMyCarsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { onItemClicked(getItem(position)) }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Vehiculo>() {
        override fun areItemsTheSame(oldItem: Vehiculo, newItem: Vehiculo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Vehiculo, newItem: Vehiculo): Boolean {
            return oldItem.marca == newItem.marca
                    && oldItem.modelo == newItem.modelo
                    && oldItem.year == newItem.year
                    && oldItem.placa == newItem.placa
                    && oldItem.path == newItem.path
                    && oldItem.estado == newItem.estado
        }

    }
}