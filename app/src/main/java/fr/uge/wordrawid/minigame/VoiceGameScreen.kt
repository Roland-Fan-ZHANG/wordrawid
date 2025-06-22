package fr.uge.wordrawid.minigame

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.*
import kotlin.math.absoluteValue

@Composable
@androidx.annotation.RequiresPermission(Manifest.permission.RECORD_AUDIO)
fun BalloonGameScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var barHeight by remember { mutableFloatStateOf(10f) }
    var isRecording by remember { mutableStateOf(false) }
    var gameEnded by remember { mutableStateOf(false) }
    val gameDurationMillis = 5000L
    var gameResult by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableIntStateOf((gameDurationMillis / 1000).toInt()) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                startRecording(scope, onAmplitude = { amplitude ->
                    if (!gameEnded) {
                        barHeight = (10f + amplitude * 5).coerceAtMost(300f)
                        if (barHeight >= 250f) {
                            gameResult = true
                            gameEnded = true
                        }
                    }
                })
                isRecording = true
            }
        }
    )

    LaunchedEffect(Unit) {
        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        while (timeLeft > 0 && !gameEnded) {
            delay(1000L)
            timeLeft -= 1
        }
        if (!gameEnded) {
            gameResult = false
            gameEnded = true
        }
    }

    LaunchedEffect(gameEnded) {
        if (gameEnded) {
            delay(1000L)
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("minigameResult", gameResult)
            navController.navigateUp()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Parler pour monter la barre", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Temps restant : $timeLeft s", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .width(60.dp)
                .height(300.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight.dp)
                    .background(Color.Cyan)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@androidx.annotation.RequiresPermission(Manifest.permission.RECORD_AUDIO)
fun startRecording(scope: CoroutineScope, onAmplitude: (Float) -> Unit) {
    scope.launch {
        val bufferSize = AudioRecord.getMinBufferSize(
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        val buffer = ShortArray(bufferSize)
        audioRecord.startRecording()

        withContext(Dispatchers.Default) {
            while (true) {
                val read = audioRecord.read(buffer, 0, bufferSize)
                if (read > 0) {
                    val max = buffer.take(read).maxOf { it.toInt().absoluteValue }
                    onAmplitude(max / 32768f * 100)
                }
                delay(50)
            }
        }
    }
}