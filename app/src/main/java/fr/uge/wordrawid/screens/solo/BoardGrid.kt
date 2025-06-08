package fr.uge.wordrawid.ui.screens.solo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Plateau de jeu 5x5 qui masque l’image derrière et dévoile les cases au clic
 */
@Composable
fun BoardGrid(caseMasquee: SnapshotStateList<Boolean>) {
    Box(
        modifier = Modifier
            .size(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            repeat(5) { row ->
                Row(modifier = Modifier.weight(1f)) {
                    repeat(5) { col ->
                        val index = row * 5 + col
                        if (caseMasquee[index]) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                color = Color.Black,
                                border = BorderStroke(1.dp, Color.White),
                                shape = RoundedCornerShape(0.dp)
                            ) {}
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }
            }
        }
    }
}
