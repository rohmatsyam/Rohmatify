package com.example.rohmatify.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.example.rohmatify.R
import com.example.rohmatify.adapters.SwipeSongAdapter
import com.example.rohmatify.data.entities.Song
import com.example.rohmatify.databinding.ActivityMainBinding
import com.example.rohmatify.exoplayer.isPlaying
import com.example.rohmatify.exoplayer.toSong
import com.example.rohmatify.other.Status
import com.example.rohmatify.ui.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
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

    private var playbackState:PlaybackStateCompat ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeToObservers()

        binding.vpSong.adapter = swipeSongAdapter

        binding.vpSong.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(playbackState?.isPlaying == true){
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                }else{
                    curPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })

        binding.ivPlayPause.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it,true)
            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        swipeSongAdapter.setItemClickListener {
            navController.navigate(
                R.id.globalActionToSongFragment
            )
        }

        navController.addOnDestinationChangedListener { _, destination,_ ->
            when(destination.id){
                R.id.songFragment -> hideBottomBar()
                R.id.homeFragment -> showBottomBar()
                else -> showBottomBar()
            }
        }

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
        mainViewModel.playbackState.observe(this){
            playbackState = it
            binding.ivPlayPause.setImageResource(
                if(playbackState?.isPlaying ==true) R.drawable.ic_pause else R.drawable.ic_play
            )
        }
        mainViewModel.isConnected.observe(this){
            it?.getContentIfNotHandled()?.let { result->
                when(result.status){
                    Status.ERROR -> Snackbar.make(
                        binding.rootLayout,
                        result.message?:"An unknown error occured",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else ->Unit
                }
            }
        }
        mainViewModel.neworkError.observe(this){
            it?.getContentIfNotHandled()?.let { result->
                when(result.status){
                    Status.ERROR -> Snackbar.make(
                        binding.rootLayout,
                        result.message?:"An unknown error occured",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else ->Unit
                }
            }
        }
    }

    private fun switchViewPagerToCurrentSong(song: Song){
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if(newItemIndex != -1){
            binding.vpSong.currentItem = newItemIndex
            curPlayingSong = song
        }
    }

    private fun hideBottomBar(){
        binding.ivCurSongImage.isVisible = false
        binding.vpSong.isVisible = false
        binding.ivPlayPause.isVisible = false
    }
    private fun showBottomBar(){
        binding.ivCurSongImage.isVisible = true
        binding.vpSong.isVisible = true
        binding.ivPlayPause.isVisible = true
    }
}