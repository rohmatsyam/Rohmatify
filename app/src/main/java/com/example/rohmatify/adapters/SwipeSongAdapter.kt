package com.example.rohmatify.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import com.example.rohmatify.data.entities.Song
import com.example.rohmatify.databinding.ListItemBinding

class SwipeSongAdapter : BaseSongAdapter() {
	private lateinit var binding: ListItemBinding

	override val differ = AsyncListDiffer(this, diffCallback)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
		binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return SongViewHolder(binding)
	}

	override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
		val song = songs[position]
		holder.itemView.apply {
			val text = "${song.title} - ${song.subtitle}"
			binding.tvPrimary.text = text

			setOnClickListener {
				onItemClickListener?.let { click ->
					click(song)
				}
			}
		}
	}

	fun setItemClickListener(listener: (Song) -> Unit) {
		onItemClickListener = listener
	}
}