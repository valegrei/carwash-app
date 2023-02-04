package pe.com.carwashperuapp.carwashapp.ui.reserve_list_dis

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.databinding.ItemServiceReserveDetailBinding
import pe.com.carwashperuapp.carwashapp.model.ServicioEstado
import pe.com.carwashperuapp.carwashapp.model.ServicioReserva

class ServiceDisListAdapter(val muestraBoton: Boolean) :
    ListAdapter<ServicioReserva, ServiceDisListAdapter.ViewHolder>(DiffCallback) {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val binding: ItemServiceReserveDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(servicio: ServicioReserva, muestraBoton: Boolean) {
            binding.apply {
                binding.servicio = servicio
                binding.executePendingBindings()
                binding.btnOpciones.setOnClickListener {
                    showMenu(it, R.menu.popup_states_menu)
                }
                if (muestraBoton) {
                    binding.btnOpciones.visibility = View.VISIBLE
                } else {
                    binding.btnOpciones.visibility = View.GONE
                }
            }
        }

        private fun showMenu(v: View, @MenuRes menuRes: Int) {
            val popup = PopupMenu(v.context, v)
            popup.menuInflater.inflate(menuRes, popup.menu)

            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                val servicio = binding.servicio!!
                // Respond to menu item click.
                when (menuItem.itemId) {
                    R.id.opt_no_atendido -> servicio.detalle?.estado = ServicioEstado.NO_ATENDIDO.id
                    R.id.opt_atendido -> servicio.detalle?.estado = ServicioEstado.ATENDIDO.id
                    R.id.opt_anulado -> servicio.detalle?.estado = ServicioEstado.ANULADO.id
                }
                binding.servicio = servicio
                binding.executePendingBindings()
                true
            }
            popup.setOnDismissListener {
                // Respond to popup being dismissed.
            }
            // Show the popup menu.
            popup.show()
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemServiceReserveDetailBinding.inflate(
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
        holder.bind(getItem(holder.adapterPosition), muestraBoton)
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