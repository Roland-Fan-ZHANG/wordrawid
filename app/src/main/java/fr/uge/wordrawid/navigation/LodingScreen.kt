package fr.uge.wordrawid.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoadingScreen() {
  BackHandler {
    // Ne rien faire, bloque le retour arrière
  }
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White)
      .clickable(enabled = true, onClick = {}), // Bloque tous les clics
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      CircularProgressIndicator()
      Spacer(modifier = Modifier.height(8.dp))
      Text("Chargement...")
    }
  }
}
