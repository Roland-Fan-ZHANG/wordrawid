package fr.uge.wordrawid.multi

import android.graphics.BitmapFactory
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.io.File

@Composable
fun LocalImage(file: File, modifier: Modifier = Modifier) {
  val bitmap = remember(file) {
    BitmapFactory.decodeFile(file.absolutePath)
  }

  bitmap?.let {
    Image(
      bitmap = it.asImageBitmap(),
      contentDescription = "Image locale",
      modifier = modifier
    )
  }
}

@Composable
fun GameScreen(gameId: Long, navController: NavController) {
  val context = LocalContext.current
  val file = File(context.cacheDir, "image_$gameId.jpg")

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    LocalImage(file = file, modifier = Modifier.fillMaxSize())
  }
}
