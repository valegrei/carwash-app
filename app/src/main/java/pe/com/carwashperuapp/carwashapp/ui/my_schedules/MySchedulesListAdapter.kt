package pe.com.carwashperuapp.carwashapp.ui.my_schedules

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pe.com.carwashperuapp.carwashapp.database.horario.HorarioConfigLocal
import pe.com.carwashperuapp.carwashapp.databinding.ItemMySchedulesBinding

class MySchedulesListAdapter(private val onItemClicked: (HorarioConfigLocal) -> Unit) :
    ListAdapter<HorarioConfigLocal, MySchedulesListAdapter.ViewHolder>(DiffCallback) {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val binding: ItemMySchedulesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(horarioConfig: HorarioConfigLocal) {
            binding.apply {
                binding.horarioConfig = horarioConfig
                binding.executePendingBindings()
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMySchedulesBinding =
            ItemMySchedulesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.bind(getItem(holder.adapterPosition))
        holder.itemView.setOnClickListener { onItemClicked(getItem(holder.adapterPosition)) }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<HorarioConfigLocal>() {
        override fun areItemsTheSame(
            oldItem: HorarioConfigLocal,
            newItem: HorarioConfigLocal
        ): Boolean {
            return oldItem.horarioConfig.id == newItem.horarioConfig.id
        }

        override fun areContentsTheSame(
            oldItem: HorarioConfigLocal,
            newItem: HorarioConfigLocal
        ): Boolean {
            return oldItem.horarioConfig.lunes == newItem.horarioConfig.lunes
                    && oldItem.horarioConfig.martes == newItem.horarioConfig.martes
                    && oldItem.horarioConfig.miercoles == newItem.horarioConfig.miercoles
                    && oldItem.horarioConfig.jueves == newItem.horarioConfig.jueves
                    && oldItem.horarioConfig.viernes == newItem.horarioConfig.viernes
                    && oldItem.horarioConfig.sabado == newItem.horarioConfig.sabado
                    && oldItem.horarioConfig.domingo == newItem.horarioConfig.domingo
                    && oldItem.horarioConfig.horaIni == newItem.horarioConfig.horaIni
                    && oldItem.horarioConfig.horaFin == newItem.horarioConfig.horaFin
                    && oldItem.horarioConfig.nroAtenciones == newItem.horarioConfig.nroAtenciones
                    && oldItem.horarioConfig.estado == newItem.horarioConfig.estado
        }

    }
}