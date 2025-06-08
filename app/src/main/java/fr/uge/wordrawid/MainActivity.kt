package fr.uge.wordrawid

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import fr.uge.wordrawid.ui.theme.WordrawidTheme
import fr.uge.wordrawid.navigation.AppNavGraph
import fr.uge.wordrawid.screens.multi.StompClientManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StompClientManager.initialize(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
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
