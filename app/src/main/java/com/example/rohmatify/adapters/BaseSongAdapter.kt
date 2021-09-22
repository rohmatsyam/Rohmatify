package com.example.rohmatify.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.rohmatify.data.entities.Song

abstract class BaseSongAdapter : RecyclerView.Adapter<BaseSongAdapter.SongViewHolder>() {

	class SongViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

	protected val diffCallback = object : DiffUtil.ItemCallback<Song>() {
		override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
			return oldItem.mediaId == newItem.mediaId
		}

		override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
			return oldItem.hashCode() == newItem.hashCode()
		}
	}

	protected abstract val differ: AsyncListDiffer<Song>

	var songs: List<Song>
		get() = differ.currentList
		set(value) = differ.submitList(value)

	protected var onItemClickListener: ((Song) -> Unit)? = null

	override fun getItemCount(): Int {
		return songs.size
	}

	fun setItemClickListener(listener: (Song) -> Unit) {
		onItemClickListener = listener
	}
}