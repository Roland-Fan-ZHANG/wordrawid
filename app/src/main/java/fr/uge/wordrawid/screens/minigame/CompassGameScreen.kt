package fr.uge.wordrawid.screens.minigame

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.uge.wordrawid.R
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun CompassGameScreen(navController: NavController) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val rotation = remember { mutableFloatStateOf(0f) }
    val targetRotation = remember { mutableFloatStateOf(Random.nextFloat() * 360f - 180f) }
    val currentRound = remember { mutableIntStateOf(0) }
    val totalRounds = 4
    val goalReached = remember { mutableStateOf(false) }
    val timer = remember { mutableIntStateOf(30) }

    LaunchedEffect(Unit) {
        while (timer.intValue > 0 && !goalReached.value) {
            kotlinx.coroutines.delay(1000)
            timer.intValue--
        }
        if (timer.intValue <= 0 && !goalReached.value) {
            navController.previousBackStackEntry?.savedStateHandle?.set("minigameResult", false)
            navController.navigateUp()
        }
    }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]

                    val azimuth = atan2(-x, y) * (180 / Math.PI).toFloat()
                    rotation.floatValue = azimuth

                    if (Math.abs(rotation.floatValue - targetRotation.floatValue) < 10f && !goalReached.value) {
                        currentRound.intValue++
                        if (currentRound.intValue >= totalRounds) {
                            goalReached.value = true
                            navController.previousBackStackEntry?.savedStateHandle?.set("minigameResult", true)
                            navController.navigateUp()
                        } else {
                            targetRotation.floatValue = Random.nextFloat() * 360f - 180f
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(listener, accel, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.compass),
                contentDescription = "Compass",
                modifier = Modifier
                    .size(300.dp)
                    .graphicsLayer(rotationZ = rotation.floatValue)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Rotation : ${rotation.floatValue.roundToInt()}°", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Cible : ${targetRotation.floatValue.roundToInt()}°", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Progression : ${currentRound.intValue}/$totalRounds", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Temps restant : ${timer.intValue} s", fontSize = 18.sp)
        }
    }

    val activity = LocalContext.current as? Activity
    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
