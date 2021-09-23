package com.example.rohmatify.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.example.rohmatify.data.entities.Song
import com.example.rohmatify.databinding.FragmentSongBinding
import com.example.rohmatify.exoplayer.toSong
import com.example.rohmatify.other.Status
import com.example.rohmatify.ui.viewmodels.MainViewModel
import com.example.rohmatify.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment:Fragment() {

	@Inject
	lateinit var glide:RequestManager

	private lateinit var mainViewModel: MainViewModel
	private lateinit var binding:FragmentSongBinding

	private val songViewModel:SongViewModel by viewModels()
	private var curPlayingSong: Song ?= null

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = FragmentSongBinding.inflate(layoutInflater)

		mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

		subscribeToObservers()

		return binding.root
	}

	private fun updateTitleAndSongImage(song: Song){
		val title = "${song.title} - ${song.subtitle}"
		binding.tvSongName.text = title
		glide.load(song.imageUrl).into(binding.ivSongImage)
	}

	private fun subscribeToObservers(){
		mainViewModel.mediaItem.observe(viewLifecycleOwner){
			it?.let { result ->
				when(result.status){
					Status.SUCCESS -> {
						result.data?.let { songs ->
							if(curPlayingSong == null && songs.isNotEmpty()){
								curPlayingSong = songs[0]
								updateTitleAndSongImage(songs[0])
							}
						}
					}
					else -> Unit
				}
			}
		}
		mainViewModel.curPlayingSong.observe(viewLifecycleOwner){
			if(it == null) return@observe
			curPlayingSong = it.toSong()
			updateTitleAndSongImage(curPlayingSong!!)
		}
	}
}