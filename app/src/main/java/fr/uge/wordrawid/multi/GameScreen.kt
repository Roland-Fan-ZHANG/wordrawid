package fr.uge.wordrawid.multi

import android.graphics.BitmapFactory
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.uge.wordrawid.navigation.GameSharedViewModel
import fr.uge.wordrawid.navigation.LoadingScreen
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
fun GameScreen(gameId: Long,
               navController: NavController,
               gameSharedViewModel: GameSharedViewModel) {
  val context = LocalContext.current
  val gameData = gameSharedViewModel.currentGameData
  val image = File(context.cacheDir, "image_$gameId.jpg")
  if (gameData == null) {
    LoadingScreen()
    return
  }

  // TODO : Afficher plateau de jeu et joueurs
  
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Afficher l'image locale
    LocalImage(
      file = image,
      modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .clip(RoundedCornerShape(8.dp))
    )
    Spacer(Modifier.height(16.dp))
    
    // Infos de debug 
    Text("🎮 Code de la partie : ${gameData.joinCode}", fontSize = 20.sp)
    Text("👑 Admin : ${gameData.adminName}")
    Text("📌 État de la partie : ${gameData.gameState}")
    Spacer(Modifier.height(16.dp))
    Text("👥 Joueurs :", fontWeight = FontWeight.Bold)
    gameData.players.forEach { player ->
      Text("- ${player.name} (ID: ${player.id}) — Position : ${player.position}")
    }
    Spacer(Modifier.height(16.dp))
    Text("🎯 Mot final : ${gameData.gameManager.finalWord.word}", fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    Text("🧩 Plateau (${gameData.gameManager.board.size} cases) :", fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    Spacer(Modifier.height(16.dp))
    Text("🔄 Joueur en cours : ${gameData.players.getOrNull(gameData.gameManager.currentPlayerIndex)?.name ?: "?"}")
  }
}
