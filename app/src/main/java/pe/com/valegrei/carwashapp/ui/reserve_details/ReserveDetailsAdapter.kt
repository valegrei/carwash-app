package pe.com.valegrei.carwashapp.ui.reserve_details

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.databinding.ItemReserveDetailBinding
import pe.com.valegrei.carwashapp.model.SERVICE_STATE_ATTENDED
import pe.com.valegrei.carwashapp.model.SERVICE_STATE_CANCELED
import pe.com.valegrei.carwashapp.model.SERVICE_STATE_PENDING
import pe.com.valegrei.carwashapp.model.Servicio

class ReserveDetailsAdapter(
    private val context: Context,
    private val dataSet: Array<Servicio>,
    private var listener: OnInteractionListener
) :
    RecyclerView.Adapter<ReserveDetailsAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val binding: ItemReserveDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(context: Context, data: Servicio, listener: OnInteractionListener) {
            binding.apply {
                tvServiceName.text = data.name
                tvServiceSchedule.text = data.schedule
                tvServicePrice.text = String.format("S/ %.2f", data.price)
                var stateColor = 0
                var stateName = 0
                when (data.state) {
                    SERVICE_STATE_ATTENDED -> {
                        stateColor = R.color.service_state_attended
                        stateName = R.string.service_state_attended
                    }
                    SERVICE_STATE_PENDING -> {
                        stateColor = R.color.service_state_pending
                        stateName = R.string.service_state_pending
                    }
                    SERVICE_STATE_CANCELED -> {
                        stateColor = R.color.service_state_canceled
                        stateName = R.string.service_state_canceled
                    }
                }
                lyState.setBackgroundColor(context.getColor(stateColor))
                tvServiceState.setText(stateName)
                cardview.setOnClickListener { listener.onClick(data) }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemReserveDetailBinding =
            ItemReserveDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.bindData(context, dataSet[position], listener)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    interface OnInteractionListener {
        fun onClick(item: Servicio)
    }
}