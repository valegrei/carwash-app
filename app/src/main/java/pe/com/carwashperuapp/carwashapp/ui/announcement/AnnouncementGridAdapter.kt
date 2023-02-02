package pe.com.carwashperuapp.carwashapp.ui.announcement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pe.com.carwashperuapp.carwashapp.database.anuncio.Anuncio
import pe.com.carwashperuapp.carwashapp.databinding.ItemAnnounceBinding

class AnnouncementGridAdapter(private val onItemClicked: (Anuncio) -> Unit) :
    ListAdapter<Anuncio, AnnouncementGridAdapter.AnnouncementViewHolder>(DiffCallback) {

    class AnnouncementViewHolder(
        private var binding: ItemAnnounceBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(anuncio: Anuncio) {
            binding.anuncio = anuncio
            /*binding.cardAnuncio.setOnLongClickListener {
                binding.cardAnuncio.toggle()
                true
            }*/
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Anuncio>() {
        override fun areItemsTheSame(oldItem: Anuncio, newItem: Anuncio): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Anuncio, newItem: Anuncio): Boolean {
            return oldItem.path == newItem.path
                    && oldItem.descripcion == newItem.descripcion
                    && oldItem.url == newItem.url
                    && oldItem.estado == newItem.estado
                    && oldItem.mostrar == newItem.mostrar
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        return AnnouncementViewHolder(
            ItemAnnounceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
        holder.itemView.setOnClickListener { onItemClicked(getItem(holder.adapterPosition)) }
    }

}