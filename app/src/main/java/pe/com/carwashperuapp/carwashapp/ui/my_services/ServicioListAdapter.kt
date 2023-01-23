package pe.com.carwashperuapp.carwashapp.ui.my_services

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pe.com.carwashperuapp.carwashapp.database.servicio.Servicio
import pe.com.carwashperuapp.carwashapp.databinding.ItemServiceListBinding

class ServicioListAdapter(private val onItemClicked: (Servicio) -> Unit) :
    ListAdapter<Servicio, ServicioListAdapter.ServicioViewHolder>(DiffCallback) {

    class ServicioViewHolder(
        private var binding: ItemServiceListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(servicio: Servicio) {
            binding.servicio = servicio
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Servicio>() {
        override fun areItemsTheSame(oldItem: Servicio, newItem: Servicio): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Servicio, newItem: Servicio): Boolean {
            return oldItem.nombre == newItem.nombre && oldItem.precio == newItem.precio
                    && oldItem.estado == newItem.estado
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        return ServicioViewHolder(
            ItemServiceListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
        holder.itemView.setOnClickListener { onItemClicked(getItem(holder.adapterPosition)) }
    }

}