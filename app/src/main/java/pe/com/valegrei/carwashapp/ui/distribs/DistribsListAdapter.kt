package pe.com.valegrei.carwashapp.ui.distribs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.databinding.ItemDistribsBinding
import pe.com.valegrei.carwashapp.ui.util.FilterableListAdapter

class DistribsListAdapter(private val onItemClicked: (Usuario) -> Unit) :
    FilterableListAdapter<Usuario, DistribsListAdapter.DistribViewHolder>(DiffCallback) {

    class DistribViewHolder(
        private var binding: ItemDistribsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(usuario: Usuario) {
            binding.distrib = usuario
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Usuario>() {
        override fun areItemsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem.correo == newItem.correo && oldItem.razonSocial == newItem.razonSocial
                    && oldItem.nroDocumento == newItem.nroDocumento && oldItem.distAct == newItem.distAct
                    && oldItem.estado == newItem.estado
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistribViewHolder {
        return DistribViewHolder(
            ItemDistribsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DistribViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { onItemClicked(getItem(position)) }
    }

    override fun onFilter(list: List<Usuario>, constraint: String): List<Usuario> {
        return list.filter {
            (it.nroDocumento ?: "").startsWith(constraint)
                    || (it.razonSocial ?: "").contains(constraint, true)
                    || (it.correo).startsWith(constraint, true)
        }
    }
}