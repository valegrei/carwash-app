package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pe.com.carwashperuapp.carwashapp.databinding.ItemFavoritoBinding
import pe.com.carwashperuapp.carwashapp.model.Local

class FavoritesListAdapter(private val onItemClicked: (Local) -> Unit) :
    ListAdapter<Local, FavoritesListAdapter.ViewHolder>(DiffCallback) {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val binding: ItemFavoritoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(local: Local) {
            binding.apply {
                binding.local = local
                binding.executePendingBindings()
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemFavoritoBinding =
            ItemFavoritoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.bind(getItem(holder.adapterPosition))
        holder.itemView.setOnClickListener { onItemClicked(getItem(holder.adapterPosition)) }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Local>() {
        override fun areItemsTheSame(oldItem: Local, newItem: Local): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Local, newItem: Local): Boolean {
            return oldItem.direccion == newItem.direccion
                    && oldItem.distrib?.razonSocial == oldItem.distrib?.razonSocial
        }

    }
}