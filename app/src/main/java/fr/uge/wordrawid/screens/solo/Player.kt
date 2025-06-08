package fr.uge.wordrawid.screens.solo

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import fr.uge.wordrawid.R

/**
 * Affiche le joueur avec une animation entre les cases
 */
@Composable
fun Player(position: Int) {
    val cellSize = 400.dp / 5
    val row = position / 5
    val col = position % 5

    val offsetX: Dp by animateDpAsState(targetValue = col * cellSize)
    val offsetY: Dp by animateDpAsState(targetValue = row * cellSize)

    Box(
        modifier = Modifier.size(400.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Image(
            painter = painterResource(id = R.drawable.player),
            contentDescription = "Joueur",
            modifier = Modifier
                .size(80.dp)
                .offset(x = offsetX, y = offsetY)
        )
    }
}
