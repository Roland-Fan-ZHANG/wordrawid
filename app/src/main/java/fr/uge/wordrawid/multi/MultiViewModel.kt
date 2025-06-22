package fr.uge.wordrawid.multi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import fr.uge.wordrawid.model.Lobby
import fr.uge.wordrawid.model.Player

class MultiViewModel : ViewModel() {
  var currentGameData: Lobby? = null
  var hasWon by mutableStateOf(false)
  var guess by mutableStateOf("")
  var gameMessage by mutableStateOf("")
  val hiddenCells = SnapshotStateList<Boolean>().apply {
    addAll(List(25) { true })
  }
  var currentActionText by mutableStateOf("")
  var rolling by mutableStateOf(false)
  var diceResult by mutableIntStateOf(1)
  var displayResult by mutableIntStateOf(1)
  var playerPositions = SnapshotStateList<Int>().apply {
    addAll(List(4) { 0 }) // Assuming 4 players, adjust as necessary
  }
}
