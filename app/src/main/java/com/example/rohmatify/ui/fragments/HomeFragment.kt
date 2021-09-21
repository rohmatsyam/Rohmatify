package com.example.rohmatify.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rohmatify.adapters.SongAdapter
import com.example.rohmatify.databinding.FragmentHomeBinding
import com.example.rohmatify.other.Status
import com.example.rohmatify.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment:Fragment() {
	lateinit var binding: FragmentHomeBinding
	lateinit var mainViewModel: MainViewModel

	@Inject
	lateinit var songAdapter: SongAdapter

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = FragmentHomeBinding.inflate(layoutInflater)

		mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

		setupRecyclerView()
		subscribeToObservers()

		songAdapter.setOnItemClickListener {
			mainViewModel.playOrToggleSong(it)
		}

		return binding.root
	}

	private fun setupRecyclerView() = binding.rvAllSongs.apply {
		adapter = songAdapter
		layoutManager = LinearLayoutManager(requireContext())
	}

	private fun subscribeToObservers(){
		mainViewModel.mediaItem.observe(viewLifecycleOwner){ result ->
			when(result.status){
				Status.SUCCESS -> {
					binding.allSongsProgressBar.isVisible = false
					result.data?.let { songs ->
						songAdapter.songs = songs
					}
				}
				Status.ERROR -> Unit
				Status.LOADING -> binding.allSongsProgressBar.isVisible = true
			}
		}
	}
}