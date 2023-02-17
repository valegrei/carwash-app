package pe.com.carwashperuapp.carwashapp.ui.reserve_list_admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pe.com.carwashperuapp.carwashapp.databinding.ItemReserveAdminBinding
import pe.com.carwashperuapp.carwashapp.databinding.ItemReserveDisBinding
import pe.com.carwashperuapp.carwashapp.model.Reserva

class ReserveListAdminAdapter(private val onItemClicked: (Reserva) -> Unit) :
    ListAdapter<Reserva, ReserveListAdminAdapter.ViewHolder>(DiffCallback) {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val binding: ItemReserveAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reserva: Reserva) {
            binding.apply {
                binding.reserva = reserva
                binding.executePendingBindings()
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemReserveAdminBinding =
            ItemReserveAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.bind(getItem(holder.adapterPosition))
        holder.itemView.setOnClickListener { onItemClicked(getItem(holder.adapterPosition)) }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Reserva>() {
        override fun areItemsTheSame(oldItem: Reserva, newItem: Reserva): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Reserva, newItem: Reserva): Boolean {
            return oldItem.id == newItem.id
                    && (oldItem.cliente?.nombres
                    == newItem.cliente?.nombres)
                    && (oldItem.cliente?.apellidoPaterno
                    == newItem.cliente?.apellidoPaterno)
                    && (oldItem.cliente?.apellidoMaterno
                    == newItem.cliente?.apellidoMaterno)
                    && (oldItem.local?.direccion
                    == newItem.local?.direccion)
                    && (oldItem.vehiculo?.marca
                    == newItem.vehiculo?.marca)
                    && (oldItem.vehiculo?.modelo
                    == newItem.vehiculo?.modelo)
                    && (oldItem.vehiculo?.year
                    == newItem.vehiculo?.year)
                    && (oldItem.vehiculo?.placa
                    == newItem.vehiculo?.placa)
                    && (oldItem.fecha
                    == newItem.fecha)
                    && (oldItem.horaIni
                    == newItem.horaIni)
                    && (oldItem.estadoAtencion
                    == newItem.estadoAtencion)
        }

    }
}