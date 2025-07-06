package com.example.musicplayer

data class AudioFile(
    val id: Long,
    val title: String,
    val artist: String?,
    val data: String,
    val dateAdded: Long
) 