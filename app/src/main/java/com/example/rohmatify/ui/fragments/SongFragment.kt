package com.example.rohmatify.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.example.rohmatify.R
import com.example.rohmatify.data.entities.Song
import com.example.rohmatify.databinding.FragmentSongBinding
import com.example.rohmatify.exoplayer.isPlaying
import com.example.rohmatify.exoplayer.toSong
import com.example.rohmatify.other.Status
import com.example.rohmatify.ui.viewmodels.MainViewModel
import com.example.rohmatify.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment:Fragment() {

	@Inject
	lateinit var glide:RequestManager

	private lateinit var mainViewModel: MainViewModel
	private lateinit var binding:FragmentSongBinding

	private val songViewModel:SongViewModel by viewModels()
	private var curPlayingSong: Song ?= null

	private var playbackState:PlaybackStateCompat ?= null

	private var shouldUpdateSeekBar = true

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = FragmentSongBinding.inflate(layoutInflater)

		mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

		subscribeToObservers()

		binding.ivPlayPauseDetail.setOnClickListener {
			curPlayingSong?.let {
				mainViewModel.playOrToggleSong(it,true)
			}
		}

		binding.seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
			override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
				if(fromUser){
					setCurPlayerTimeToTextView(progress.toLong())
				}
			}

			override fun onStartTrackingTouch(p0: SeekBar?) {
				shouldUpdateSeekBar = false
			}

			override fun onStopTrackingTouch(seekBar: SeekBar?) {
				seekBar?.let {
					mainViewModel.seekTo(it.progress.toLong())
					shouldUpdateSeekBar = true
				}
			}
		})

		binding.ivSkipPrevious.setOnClickListener {
			mainViewModel.skipToPreviousSong()
		}

		binding.ivSkip.setOnClickListener {
			mainViewModel.skipToNextSong()
		}

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

		mainViewModel.playbackState.observe(viewLifecycleOwner){
			playbackState = it
			binding.ivPlayPauseDetail.setImageResource(
				if(playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
			)
			binding.seekBar.progress = it?.position?.toInt() ?: 0
		}
		songViewModel.curPlayerPosition.observe(viewLifecycleOwner){
			if(shouldUpdateSeekBar){
				binding.seekBar.progress = it.toInt()
				setCurPlayerTimeToTextView(it)
			}
		}
		songViewModel.curSongDuration.observe(viewLifecycleOwner){
			binding.seekBar.max = it.toInt()
			val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
			binding.tvSongDuration.text = dateFormat.format(it)
		}
	}
	private fun setCurPlayerTimeToTextView(ms:Long){
		val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
		binding.tvCurTime.text = dateFormat.format(ms)
	}
}