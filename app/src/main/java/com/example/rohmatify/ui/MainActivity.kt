package com.example.rohmatify.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.RequestManager
import com.example.rohmatify.adapters.SwipeSongAdapter
import com.example.rohmatify.data.entities.Song
import com.example.rohmatify.databinding.ActivityMainBinding
import com.example.rohmatify.exoplayer.toSong
import com.example.rohmatify.other.Status
import com.example.rohmatify.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    private val mainViewModel:MainViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    private var curPlayingSong:Song? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeToObservers()

        binding.vpSong.adapter = swipeSongAdapter


    }

    private fun subscribeToObservers(){
        mainViewModel.mediaItem.observe(this){
            it?.let { result ->
                when(result.status){
                    Status.SUCCESS ->{
                        result.data?.let { songs->
                            swipeSongAdapter.songs = songs
                            if(songs.isNotEmpty()){
                                glide.load((curPlayingSong ?: songs[0]).imageUrl).into(binding.ivCurSongImage)
                            }
                            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                        }
                    }
                    Status.ERROR->Unit
                    Status.LOADING -> Unit
                }
            }
        }
        mainViewModel.curPlayingSong.observe(this){
            if(it == null) return@observe

            curPlayingSong = it.toSong()
            glide.load(curPlayingSong?.imageUrl).into(binding.ivCurSongImage)
            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
        }
    }

    private fun switchViewPagerToCurrentSong(song: Song){
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if(newItemIndex != -1){
            binding.vpSong.currentItem = newItemIndex
            curPlayingSong = song
        }
    }
}