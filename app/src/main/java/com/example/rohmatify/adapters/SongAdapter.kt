package com.example.rohmatify.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.rohmatify.data.entities.Song
import com.example.rohmatify.databinding.ListItemBinding
import javax.inject.Inject

class SongAdapter @Inject constructor(
	private val glide: RequestManager
) :RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

	private lateinit var binding: ListItemBinding

	class SongViewHolder(itemView:ListItemBinding) : RecyclerView.ViewHolder(itemView.root)

	private val diffCallback = object : DiffUtil.ItemCallback<Song>(){
		override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
			return oldItem.mediaId == newItem.mediaId
		}

		override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
			return oldItem.hashCode() == newItem.hashCode()
		}
	}

	private val differ = AsyncListDiffer(this,diffCallback)

	var songs:List<Song>
		get() = differ.currentList
		set(value) = differ.submitList(value)

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

	private var onItemClickListener:((Song) -> Unit)? = null

	fun setOnItemClickListener(listener:(Song) -> Unit){
		onItemClickListener = listener
	}

	override fun getItemCount(): Int {
		return songs.size
	}
}