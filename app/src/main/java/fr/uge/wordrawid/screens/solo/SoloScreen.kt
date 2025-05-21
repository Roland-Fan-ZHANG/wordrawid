package fr.uge.wordrawid.ui.screens.solo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.uge.wordrawid.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Renvoie un entier aléatoire entre 1 et 6
 */
fun rollDice(): Int = Random.nextInt(1, 7)

/**
 * Affiche l’image du dé correspondant au résultat
 */
@Composable
fun DiceImage(rollResult: Int, modifier: Modifier = Modifier) {
    val imageRes = when (rollResult) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Face du dé : $rollResult",
        modifier = modifier
    )
}

/**
 * Affiche l’image animée + bouton
 * @param displayResult la face en cours d’affichage (change vite pendant l’animation)
 * @param onRoll déclenché au clic
 * @param rolling indique si on est en cours d’animation
 */
@Composable
fun DiceWithImage(
    displayResult: Int,
    onRoll: () -> Unit,
    rolling: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DiceImage(displayResult)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRoll, enabled = !rolling) {
                Text(if (rolling) "Rolling…" else "Roll Dice")
            }
        }
    }
}

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
        if (rolling) return  // ignore si déjà en cours
        rolling = true
        scope.launch {
            // petite animation de 1 s : 10 itérations à 100 ms
            repeat(10) {
                displayResult = (1..6).random()
                delay(50)
            }
            // résultat final
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
}
