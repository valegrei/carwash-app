package pe.com.carwashperuapp.carwashapp.ui.my_places

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import pe.com.carwashperuapp.carwashapp.databinding.ItemPlaceAcBinding
import pe.com.carwashperuapp.carwashapp.ui.util.formatearDistancia

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
        holder.bind(getItem(holder.adapterPosition))
        holder.itemView.setOnClickListener { onItemClicked(getItem(holder.adapterPosition)) }
    }

}