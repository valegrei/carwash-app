package pe.com.valegrei.carwashapp.ui.reserve_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pe.com.valegrei.carwashapp.databinding.ItemReserveListBinding
import pe.com.valegrei.carwashapp.model.Reserva

class ReserveListAdapter(private val dataSet: Array<Reserva>) :
    RecyclerView.Adapter<ReserveListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val binding: ItemReserveListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Reserva) {
            binding.apply {
                tvClient.text = data.client
                tvVehicle.text = data.vehicle
                tvPlace.text = data.place
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemReserveListBinding =
            ItemReserveListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.bindData(dataSet[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}