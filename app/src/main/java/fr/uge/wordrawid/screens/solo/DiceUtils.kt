package fr.uge.wordrawid.screens.solo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.uge.wordrawid.R
import kotlin.random.Random

/**
 * Renvoie un entier aléatoire entre 1 et 6
 */
fun rollDice(): Int = Random.nextInt(1, 7)

/**
 * Affiche l’image du dé correspondant au résultat, taille réduite
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
        modifier = modifier.size(100.dp)  // Taille réduite
    )
}

/**
 * Affiche l’image animée + bouton centré en bas
 */
@Composable
fun DiceWithImage(
    displayResult: Int,
    onRoll: () -> Unit,
    rolling: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 110.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DiceImage(displayResult)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRoll, enabled = !rolling) {
                Text(if (rolling) "Rolling…" else "Roll Dice")
            }
        }
    }
}
