package fr.uge.wordrawid.screens.solo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.uge.wordrawid.ui.screens.solo.BoardGrid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SoloScreen() {
    var finalResult by remember { mutableStateOf(1) }
    var displayResult by remember { mutableStateOf(1) }
    var rolling by remember { mutableStateOf(false) }
    var playerPosition by remember { mutableStateOf(0) }
    var currentActionText by remember { mutableStateOf("") }
    val caseMasquee = remember { mutableStateListOf(*Array(25) { true }) }

    val scope = rememberCoroutineScope()

    fun animateMovement(steps: Int, forward: Boolean = true) = scope.launch {
        repeat(steps) {
            playerPosition = if (forward) (playerPosition + 1) % 25 else (playerPosition - 1 + 25) % 25
            delay(200)
        }
    }

    fun handleAction(action: CaseAction) = scope.launch {
        currentActionText = when (action) {
            is CaseAction.MoveForward2 -> "Avance de 2 cases!"
            is CaseAction.MoveBackward2 -> "Recule de 2 cases!"
            is CaseAction.MiniGame -> "Mini-jeu à lancer!"
            is CaseAction.RevealTile -> "Révèle une case!"
            is CaseAction.Nothing -> "Aucune action."
        }

        when (action) {
            is CaseAction.MoveForward2 -> animateMovement(2)
            is CaseAction.MoveBackward2 -> animateMovement(2, forward = false)
            is CaseAction.RevealTile -> caseMasquee[playerPosition] = false
            is CaseAction.MiniGame -> {/* TODO */}
            is CaseAction.Nothing -> {}
        }
    }

    fun startRolling() {
        if (rolling) return
        rolling = true
        scope.launch {
            repeat(10) {
                displayResult = (1..6).random()
                delay(50)
            }
            finalResult = rollDice()
            displayResult = finalResult

            animateMovement(finalResult).join()
            handleAction(boardActions[playerPosition])
            rolling = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentActionText,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                RandomImage()
                BoardGrid(caseMasquee)
                Player(position = playerPosition)
            }
        }

        DiceWithImage(
            displayResult = displayResult,
            onRoll = { startRolling() },
            rolling = rolling
        )
    }
}
