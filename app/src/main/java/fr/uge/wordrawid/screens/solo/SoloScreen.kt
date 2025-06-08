package fr.uge.wordrawid.screens.solo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.uge.wordrawid.screens.solo.BoardGrid
import fr.uge.wordrawid.screens.solo.DiceWithImage
import fr.uge.wordrawid.screens.solo.Player
import fr.uge.wordrawid.screens.solo.RandomImage
import fr.uge.wordrawid.screens.solo.rollDice
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Écran principal avec animation, joueur, plateau et actions par case
 */
@Composable
fun SoloScreen() {
    var finalResult by remember { mutableStateOf(1) }
    var displayResult by remember { mutableStateOf(1) }
    var rolling by remember { mutableStateOf(false) }
    var playerPosition by remember { mutableStateOf(0) }
    var currentActionText by remember { mutableStateOf("") } // texte du comportement

    val scope = rememberCoroutineScope()

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

            // Animation de déplacement case par case
            repeat(finalResult) {
                playerPosition = (playerPosition + 1) % 25
                delay(200)
            }

            // Déclencher l’action de la case actuelle
            val currentAction = boardActions[playerPosition]
            currentActionText = when (currentAction) {
                is CaseAction.MoveForward2 -> "Avance de 2 cases!"
                is CaseAction.MoveBackward2 -> "Recule de 2 cases!"
                is CaseAction.MiniGame -> "Mini-jeu à lancer!"
                is CaseAction.Nothing -> "Aucune action."
            }

            when (currentAction) {
                is CaseAction.MoveForward2 -> {
                    repeat(2) {
                        delay(500)
                        playerPosition = (playerPosition + 1) % 25
                        delay(200)
                    }
                }
                is CaseAction.MoveBackward2 -> {
                    repeat(2) {
                        delay(500)
                        playerPosition = (playerPosition - 1 + 25) % 25
                        delay(200)
                    }
                }
                is CaseAction.MiniGame -> {
                    // TODO: Lancer un mini-jeu
                }
                is CaseAction.Nothing -> {
                    // Pas d’action
                }
            }
            rolling = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Texte du comportement en haut
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
                BoardGrid()
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
