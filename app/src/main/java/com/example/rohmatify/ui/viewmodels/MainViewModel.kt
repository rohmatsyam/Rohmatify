package com.example.rohmatify.ui.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rohmatify.data.entities.Song
import com.example.rohmatify.exoplayer.MusicServiceConnection
import com.example.rohmatify.exoplayer.isPlayEnable
import com.example.rohmatify.exoplayer.isPlaying
import com.example.rohmatify.exoplayer.isPrepared
import com.example.rohmatify.other.Constants
import com.example.rohmatify.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {
	private val _mediaItem = MutableLiveData<Resource<List<Song>>>()
	val mediaItem: LiveData<Resource<List<Song>>> = _mediaItem

	val isConnected = musicServiceConnection.isConnected
	val neworkError = musicServiceConnection.networkError
	val curPlayingSong = musicServiceConnection.curPlayingSong
	val playbackState = musicServiceConnection.playbackState

	init {
		_mediaItem.postValue(Resource.loading(null))
		musicServiceConnection.subscribe(Constants.MEDIA_ROOT_ID,object:MediaBrowserCompat.SubscriptionCallback(){
			override fun onChildrenLoaded(
				parentId: String,
				children: MutableList<MediaBrowserCompat.MediaItem>
			) {
				super.onChildrenLoaded(parentId, children)
				val items = children.map {
					Song(
						it.mediaId!!,
						it.description.title.toString(),
						it.description.subtitle.toString(),
						it.description.mediaUri.toString(),
						it.description.iconUri.toString()
					)
				}
				_mediaItem.postValue(Resource.success(items))
			}
		})

	}

	fun skipToNextSong(){
		musicServiceConnection.trasportControls.skipToNext()
	}

	fun skipToPreviousSong(){
		musicServiceConnection.trasportControls.skipToPrevious()
	}

	fun seekTo(pos:Long){
		musicServiceConnection.trasportControls.seekTo(pos)
	}

	fun playOrToggleSong(mediaItem: Song, toggle:Boolean = false){
		val isPrepared = playbackState.value?.isPrepared ?: false
		if(isPrepared && mediaItem.mediaId ==
			curPlayingSong.value?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)){ playbackState.value?.let { playbackState ->
				when{
					playbackState.isPlaying -> if(toggle) musicServiceConnection.trasportControls.pause()
					playbackState.isPlayEnable -> musicServiceConnection.trasportControls.play()
					else -> Unit
				}
			}
		}else{
			musicServiceConnection.trasportControls.playFromMediaId(mediaItem.mediaId,null)
		}
	}

	override fun onCleared() {
		super.onCleared()
		musicServiceConnection.unsubscribe(Constants.MEDIA_ROOT_ID, object:MediaBrowserCompat.SubscriptionCallback(){})
	}
}