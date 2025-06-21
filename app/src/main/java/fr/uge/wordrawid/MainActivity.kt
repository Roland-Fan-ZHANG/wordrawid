package fr.uge.wordrawid

import android.Manifest
import android.os.Build
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import fr.uge.wordrawid.ui.theme.WordrawidTheme
import fr.uge.wordrawid.navigation.AppNavGraph
import fr.uge.wordrawid.multi.StompClientManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @SuppressLint("SourceLockedOrientationActivity")
  @androidx.annotation.RequiresPermission(Manifest.permission.RECORD_AUDIO)
  override fun onCreate(savedInstanceState: Bundle?) {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    super.onCreate(savedInstanceState)
    StompClientManager.initialize(applicationContext)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        1
      )
    }
    setContent {
      WordrawidTheme {
        AppNavGraph()
      }
    }
  }
}
