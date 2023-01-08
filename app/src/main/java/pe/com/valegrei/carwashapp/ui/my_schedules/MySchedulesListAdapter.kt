package pe.com.valegrei.carwashapp.ui.my_schedules

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pe.com.valegrei.carwashapp.database.horario.HorarioConfig
import pe.com.valegrei.carwashapp.databinding.ItemMySchedulesBinding

class MySchedulesListAdapter(private val onItemClicked: (HorarioConfig) -> Unit) :
    ListAdapter<HorarioConfig, MySchedulesListAdapter.ViewHolder>(DiffCallback) {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val binding: ItemMySchedulesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(horarioConfig: HorarioConfig) {
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
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { onItemClicked(getItem(position)) }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<HorarioConfig>() {
        override fun areItemsTheSame(oldItem: HorarioConfig, newItem: HorarioConfig): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HorarioConfig, newItem: HorarioConfig): Boolean {
            return oldItem.lunes == newItem.lunes
                    && oldItem.martes == newItem.martes
                    && oldItem.miercoles == newItem.miercoles
                    && oldItem.jueves == newItem.jueves
                    && oldItem.viernes == newItem.viernes
                    && oldItem.sabado == newItem.sabado
                    && oldItem.domingo == newItem.domingo
                    && oldItem.horaIni == newItem.horaIni
                    && oldItem.horaFin == newItem.horaFin
                    && oldItem.intervalo == newItem.intervalo
                    && oldItem.estado == newItem.estado
        }

    }
}