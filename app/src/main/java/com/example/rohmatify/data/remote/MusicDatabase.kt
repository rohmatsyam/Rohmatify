package com.example.rohmatify.data.remote

import com.example.rohmatify.data.entities.Song
import com.example.rohmatify.other.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class MusicDatabase {
    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection =  firestore.collection(Constants.SONG_COLLECTION)

    suspend fun getAllSong():List<Song> {
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        }catch (e:Exception){
            emptyList()
        }
    }

}