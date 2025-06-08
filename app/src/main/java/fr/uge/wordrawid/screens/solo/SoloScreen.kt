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
 * Écran principal avec animation, joueur et plateau
 */
@Composable
fun SoloScreen() {
    var finalResult by remember { mutableStateOf(1) }
    var displayResult by remember { mutableStateOf(1) }
    var rolling by remember { mutableStateOf(false) }
    var playerPosition by remember { mutableStateOf(0) }

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

            // Animation de déplacement case par case avec une pause plus longue
            val steps = finalResult
            repeat(steps) {
                playerPosition = (playerPosition + 1) % 25
                delay(200) // pause de 200ms entre chaque case
            }

            rolling = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        RandomImage()
        BoardGrid()
        Player(position = playerPosition)
    }

    DiceWithImage(
        displayResult = displayResult,
        onRoll = { startRolling() },
        rolling = rolling
    )
}
