package pe.com.valegrei.carwashapp.ui.my_places

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import pe.com.valegrei.carwashapp.databinding.ItemPlaceAcBinding

class PlaceAutcompleteListAdapter(private val onItemClicked: (AutocompletePrediction) -> Unit) :
    ListAdapter<AutocompletePrediction, PlaceAutcompleteListAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(
        private var binding: ItemPlaceAcBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pred: AutocompletePrediction) {
            binding.tvDistance.text = formatearDistancia(pred.distanceMeters)
            binding.tvFirstLine.text = pred.getPrimaryText(null)
            binding.tvSecondLine.text = pred.getSecondaryText(null)
        }

        fun formatearDistancia(meters: Int?): String {
            return if (meters != null) {
                if (meters < 1000)
                    "$meters m"
                else if (meters < 10000) {
                    val km: Float = meters / 1000f
                    String.format("%.1f Km", km)
                } else if (meters < 500000) {
                    val km: Int = meters / 1000
                    String.format("%d Km", km)
                } else ""
            } else ""
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<AutocompletePrediction>() {
        override fun areItemsTheSame(
            oldItem: AutocompletePrediction,
            newItem: AutocompletePrediction
        ): Boolean {
            return oldItem.placeId == newItem.placeId
        }

        override fun areContentsTheSame(
            oldItem: AutocompletePrediction,
            newItem: AutocompletePrediction
        ): Boolean {
            return oldItem.getPrimaryText(null) == newItem.getPrimaryText(null)
                    && oldItem.getSecondaryText(null) == newItem.getSecondaryText(null)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlaceAcBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { onItemClicked(getItem(position)) }
    }

}