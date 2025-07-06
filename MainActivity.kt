package com.example.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.musicplayer.ui.theme.MusicPlayerTheme

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var currentIndex: Int by mutableStateOf(-1)
    private var audioFiles: List<AudioFile> by mutableStateOf(emptyList())

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                loadAudioFiles()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MusicPlayerScreen(
                        audioFiles = audioFiles,
                        currentIndex = currentIndex,
                        onPlay = { index -> playAudio(index) },
                        onPause = { pauseAudio() },
                        onNext = { playNext() },
                        onPrev = { playPrev() }
                    )
                }
            }
        }
        checkPermissionAndLoad()
    }

    private fun checkPermissionAndLoad() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                loadAudioFiles()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun loadAudioFiles() {
        audioFiles = AudioScanner.getAudioFiles(this)
    }

    private fun playAudio(index: Int) {
        if (index < 0 || index >= audioFiles.size) return
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioFiles[index].data)
            prepare()
            start()
        }
        currentIndex = index
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
    }

    private fun playNext() {
        if (audioFiles.isNotEmpty() && currentIndex + 1 < audioFiles.size) {
            playAudio(currentIndex + 1)
        }
    }

    private fun playPrev() {
        if (audioFiles.isNotEmpty() && currentIndex - 1 >= 0) {
            playAudio(currentIndex - 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}

@Composable
fun MusicPlayerScreen(
    audioFiles: List<AudioFile>,
    currentIndex: Int,
    onPlay: (Int) -> Unit,
    onPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Music Files", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(audioFiles) { index, audio ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlay(index) }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(audio.title, style = MaterialTheme.typography.bodyLarge)
                        Text(audio.artist ?: "Unknown Artist", style = MaterialTheme.typography.bodySmall)
                    }
                    if (index == currentIndex) {
                        Text("Playing", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onPrev) { Text("Prev") }
            Button(onClick = onPause) { Text("Pause") }
            Button(onClick = onNext) { Text("Next") }
        }
    }
} 