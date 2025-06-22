package fr.uge.wordrawid.multi

import fr.uge.wordrawid.model.Cell
import fr.uge.wordrawid.model.CellType
import fr.uge.wordrawid.model.GameManager
import fr.uge.wordrawid.model.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun handleAction(
  cell: Cell,
  position: Int,
  currentPlayerIndex: Int,
  viewModel: MultiViewModel,
  scope: CoroutineScope
) = scope.launch {
  val action = cell.cellType
  if (!viewModel.hiddenCells[position] &&
    (action == CellType.MINIGAME1 || action == CellType.MINIGAME2)) {
    viewModel.currentActionText = "Case déjà révélée, pas de mini-jeu"
    return@launch
  }
  viewModel.currentActionText = when (action) {
    CellType.BONUS -> "Avance de 2 cases"
    CellType.MALUS -> "Recule de 3 cases"
    CellType.MINIGAME1, CellType.MINIGAME2 -> "Mini-jeu"
    CellType.NEUTRAL -> "Révèle une case"
  }
  when (action) {
    CellType.BONUS -> {
      animateMovement(
        viewModel = viewModel,
        steps = 2,
        currentPlayerIndex = currentPlayerIndex,
        scope = scope
      )
      viewModel.hiddenCells[position] = false
      // TODO : Requête backend
    }
    CellType.MALUS -> {
      animateMovement(
        viewModel = viewModel,
        steps = 3,
        currentPlayerIndex = currentPlayerIndex,
        scope = scope,
        forward = false
      )
      viewModel.hiddenCells[position] = false
      // TODO : Requête backend
    }
    // TODO : Mini-jeux
    CellType.MINIGAME1, CellType.MINIGAME2 -> viewModel.hiddenCells[position] = false
    CellType.NEUTRAL -> viewModel.hiddenCells[position] = false
  }
}

fun animateMovement(
  viewModel: MultiViewModel,
  steps: Int,
  currentPlayerIndex: Int,
  scope: CoroutineScope,
  forward: Boolean = true) = scope.launch {
  repeat(steps) {
    viewModel.playerPositions[currentPlayerIndex] =
      if (forward)
        (viewModel.playerPositions[currentPlayerIndex] + 1) % 25
      else
        (viewModel.playerPositions[currentPlayerIndex] - 1 + 25) % 25
    delay(200)
  }
}

fun rolling(
  lobbyId: Long,
  player: Player,
  gameManager: GameManager,
  viewModel: MultiViewModel,
  scope: CoroutineScope
) {
  if (viewModel.rolling) return
  viewModel.rolling = true
  scope.launch {
    while (viewModel.rolling) {
      viewModel.displayResult = (1..6).random()
      delay(50)
    }
  }
  scope.launch {
    val diceRequest = rollDice(lobbyId = lobbyId, playerId = player.id)
    if (diceRequest == null) {
      viewModel.currentActionText = "Erreur lors du lancer de dé"
      viewModel.rolling = false
      return@launch
    }
    viewModel.diceResult = diceRequest.diceResult
    viewModel.displayResult = viewModel.diceResult
    viewModel.rolling = false
    animateMovement(viewModel, viewModel.diceResult, gameManager.currentPlayerIndex, scope).join()
    val nextPosition = viewModel.playerPositions[gameManager.currentPlayerIndex]
    val nextCell = gameManager.board[nextPosition]
    handleAction(nextCell, nextPosition, gameManager.currentPlayerIndex, viewModel, scope)
  }
}

fun checkGuess(
  gameManager: GameManager,
  viewModel: MultiViewModel,
) {
  if (viewModel.guess.trim().lowercase() == gameManager.finalWord.word.lowercase()) {
    viewModel.hasWon = true
  } else {
    viewModel.gameMessage = "Raté !"
  }
}
