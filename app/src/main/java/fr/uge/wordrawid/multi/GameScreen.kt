package fr.uge.wordrawid.multi

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.wordrawid.navigation.LoadingScreen
import java.io.File

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun GameScreen(
  gameId: Long,
  navController: NavController,
  multiViewModel: MultiViewModel
) {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  val gameData = multiViewModel.currentGameData
  val image = File(context.cacheDir, "image_$gameId.jpg")
  if (gameData == null) {
    LoadingScreen()
    return
  }
  val currentPlayer = gameData.players[gameData.gameManager.currentPlayerIndex]

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    maxWidth
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Column(
        modifier = Modifier
          .weight(1f)
          .verticalScroll(rememberScrollState())
          .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Spacer(modifier = Modifier.height(50.dp))
        Board(multiViewModel, image, gameData.players)

        Spacer(modifier = Modifier.height(20.dp))
        DiceSection(multiViewModel,
          onRoll = {
            rolling(
              lobbyId = gameData.id,
              player = currentPlayer,
              gameManager = gameData.gameManager,
              viewModel =  multiViewModel,
              scope = scope,
            )
          }
        )
      }

      Text(
        text = multiViewModel.currentActionText,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(4.dp)
      )
      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = multiViewModel.gameMessage,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(4.dp)
      )

      GuessSection(
        viewModel = multiViewModel,
        onCheck = {
          checkGuess(gameData.gameManager, multiViewModel)
        }
      )
      Spacer(modifier = Modifier.height(50.dp))
    }
  }

}
