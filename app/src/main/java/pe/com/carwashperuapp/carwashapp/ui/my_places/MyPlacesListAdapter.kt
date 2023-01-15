package pe.com.carwashperuapp.carwashapp.ui.my_places

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pe.com.carwashperuapp.carwashapp.database.direccion.Direccion
import pe.com.carwashperuapp.carwashapp.databinding.ItemMyPlacesBinding

class MyPlacesListAdapter(private val onItemClicked: (Direccion) -> Unit) :
    ListAdapter<Direccion, MyPlacesListAdapter.ViewHolder>(DiffCallback) {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val binding: ItemMyPlacesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(direccion: Direccion) {
            binding.apply {
                binding.local = direccion
                binding.executePendingBindings()
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
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { onItemClicked(getItem(position)) }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Direccion>() {
        override fun areItemsTheSame(oldItem: Direccion, newItem: Direccion): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Direccion, newItem: Direccion): Boolean {
            return oldItem.departamento == newItem.departamento
                    && oldItem.provincia == newItem.provincia
                    && oldItem.distrito == newItem.distrito
                    && oldItem.direccion == newItem.direccion
                    && oldItem.estado == newItem.estado
        }

    }
}