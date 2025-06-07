package fr.uge.wordrawid.ui.screens.solo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.uge.wordrawid.screens.solo.BoardGrid
import fr.uge.wordrawid.screens.solo.DiceWithImage
import fr.uge.wordrawid.screens.solo.RandomImage
import fr.uge.wordrawid.screens.solo.rollDice
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Écran principal avec animation
 */
@Composable
fun SoloScreen() {
    var finalResult by remember { mutableStateOf(1) }      // résultat « officiel »
    var displayResult by remember { mutableStateOf(1) }    // ce qu’on affiche à l’écran
    var rolling by remember { mutableStateOf(false) }      // flag animation
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
            rolling = false
        }
    }

    DiceWithImage(
        displayResult = displayResult,
        onRoll = { startRolling() },
        rolling = rolling
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        RandomImage()
        BoardGrid()
    }
}
