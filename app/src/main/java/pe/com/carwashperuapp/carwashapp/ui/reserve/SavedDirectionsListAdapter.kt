package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pe.com.carwashperuapp.carwashapp.database.direccion.Direccion
import pe.com.carwashperuapp.carwashapp.databinding.ItemPlaceSavedBinding

class SavedDirectionsListAdapter(private val onItemClicked: (Direccion) -> Unit) :
    ListAdapter<Direccion, SavedDirectionsListAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(
        private var binding: ItemPlaceSavedBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dir: Direccion) {
            binding.dir = dir
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Direccion>() {
        override fun areItemsTheSame(
            oldItem: Direccion,
            newItem: Direccion
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Direccion,
            newItem: Direccion
        ): Boolean {
            return oldItem.direccion == newItem.direccion
                    && oldItem.tipo == newItem.tipo
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlaceSavedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
        holder.itemView.setOnClickListener { onItemClicked(getItem(holder.adapterPosition)) }
    }

}