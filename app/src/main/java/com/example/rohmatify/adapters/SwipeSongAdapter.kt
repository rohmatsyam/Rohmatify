package com.example.rohmatify.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import com.example.rohmatify.databinding.SwipeItemBinding

class SwipeSongAdapter : BaseSongAdapter() {
	private lateinit var binding: SwipeItemBinding

	override val differ = AsyncListDiffer(this, diffCallback)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSongAdapter.SongViewHolder {
		binding = SwipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
}