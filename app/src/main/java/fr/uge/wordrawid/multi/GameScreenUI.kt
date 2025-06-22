package fr.uge.wordrawid.multi

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import fr.uge.wordrawid.R
import fr.uge.wordrawid.model.Player
import fr.uge.wordrawid.solo.DiceWithImage
import fr.uge.wordrawid.solo.board.BoardGrid
import java.io.File

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun Player(position: Int, index: Int) {
  val cellSize = 400.dp / 5
  val row = position / 5
  val col = position % 5
  val offsetX: Dp by animateDpAsState(targetValue = col * cellSize)
  val offsetY: Dp by animateDpAsState(targetValue = row * cellSize)
  val playerImage = when (index) {
    1 -> R.drawable.player1
    2 -> R.drawable.player2
    3 -> R.drawable.player3
    4 -> R.drawable.player4
    else -> throw IllegalArgumentException("Invalid player index: $index")
  }

  Box(
    modifier = Modifier.size(400.dp),
    contentAlignment = Alignment.TopStart
  ) {
    Image(
      painter = painterResource(id = playerImage),
      contentDescription = "Joueur",
      modifier = Modifier
        .size(80.dp)
        .offset(x = offsetX, y = offsetY)
    )
  }
}

@Composable
fun Board(
  viewModel: MultiViewModel,
  image: File,
  players: List<Player>,
  modifier: Modifier = Modifier
) {
  val bitmap = remember(image) {
    BitmapFactory.decodeFile(image.absolutePath)
  }
  Box(
    contentAlignment = Alignment.Center
  ) {
    bitmap?.let {
      Image(
        bitmap = it.asImageBitmap(),
        contentDescription = image.name,
        modifier = modifier.size(400.dp)
      )
    }
    BoardGrid(viewModel.hiddenCells)
    // Display players on the board
    players.forEach { player ->
      Player(position = player.position, index = players.indexOf(player) + 1)
    }
  }
}

@Composable
fun DiceSection(viewModel: MultiViewModel, isCurrentPlayer: Boolean, onRoll: () -> Unit) {
  if (isCurrentPlayer) {
    DiceWithImage(
      displayResult = viewModel.displayResult,
      onRoll = onRoll,
      rolling = viewModel.rolling
    )
  }
}

@Composable
fun GuessSection(viewModel: MultiViewModel, onCheck: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    OutlinedTextField(
      value = viewModel.guess,
      onValueChange = { viewModel.guess = it },
      label = { Text("Devine le mot") },
      singleLine = true,
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
      modifier = Modifier.weight(1f).padding(bottom = 20.dp)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Button(onClick = { onCheck() }) {
      Text("Valider")
    }
  }
}
