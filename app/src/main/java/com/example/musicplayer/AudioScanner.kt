package com.example.musicplayer

import android.content.Context
import android.provider.MediaStore

object AudioScanner {
    fun getAudioFiles(context: Context): List<AudioFile> {
        val audioList = mutableListOf<AudioFile>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED
        )
        val selection = (
            MediaStore.Audio.Media.MIME_TYPE + "=? OR " +
            MediaStore.Audio.Media.MIME_TYPE + "=? OR " +
            MediaStore.Audio.Media.MIME_TYPE + "=?"
        )
        val selectionArgs = arrayOf(
            "audio/mpeg", // MP3
            "audio/x-wav", // WAV
            "audio/flac"   // FLAC
        )
        val sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC"
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        cursor?.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val dateAddedCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            while (it.moveToNext()) {
                val audio = AudioFile(
                    id = it.getLong(idCol),
                    title = it.getString(titleCol),
                    artist = it.getString(artistCol),
                    data = it.getString(dataCol),
                    dateAdded = it.getLong(dateAddedCol)
                )
                audioList.add(audio)
            }
        }
        return audioList
    }
} 