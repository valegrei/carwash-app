package pe.com.carwashperuapp.carwashapp.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import pe.com.carwashperuapp.carwashapp.database.usuario.Usuario
import pe.com.carwashperuapp.carwashapp.databinding.ItemUsuariosBinding
import pe.com.carwashperuapp.carwashapp.ui.util.FilterableListAdapter

class UsersListAdapter(private val onItemClicked: (Usuario) -> Unit) :
    FilterableListAdapter<Usuario, UsersListAdapter.UsersViewHolder>(DiffCallback) {

    class UsersViewHolder(
        private var binding: ItemUsuariosBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(usuario: Usuario) {
            binding.usuario = usuario
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Usuario>() {
        override fun areItemsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem.correo == newItem.correo && oldItem.nombres == newItem.nombres
                    && oldItem.apellidoPaterno == newItem.apellidoPaterno
                    && oldItem.apellidoMaterno == newItem.apellidoMaterno
                    && oldItem.razonSocial == newItem.razonSocial
                    && oldItem.nroDocumento == newItem.nroDocumento
                    && oldItem.estado == newItem.estado
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder(
            ItemUsuariosBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
        holder.itemView.setOnClickListener { onItemClicked(getItem(holder.adapterPosition)) }
    }

    override fun onFilter(list: List<Usuario>, constraint: String): List<Usuario> {
        return list.filter {
            it.correo.startsWith(constraint, true)
        }
    }
}