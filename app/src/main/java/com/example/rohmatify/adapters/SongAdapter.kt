package com.example.rohmatify.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.example.rohmatify.databinding.ListItemBinding
import javax.inject.Inject

class SongAdapter @Inject constructor(
	private val glide: RequestManager
) :BaseSongAdapter(){
	private lateinit var binding: ListItemBinding

	override val differ = AsyncListDiffer(this, diffCallback)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
		binding = ListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
		return SongViewHolder(binding)
	}

	override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
		val song = songs[position]
		holder.itemView.apply {
			binding.tvPrimary.text = song.title
			binding.tvSecondary.text = song.subtitle
			glide.load(song.imageUrl).into(binding.ivItemImage)

			setOnClickListener {
				onItemClickListener?.let { click->
					click(song)
				}
			}
		}
	}
}