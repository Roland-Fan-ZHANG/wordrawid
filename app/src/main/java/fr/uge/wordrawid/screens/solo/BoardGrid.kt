package fr.uge.wordrawid.screens.solo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Plateau de jeu 5x5 qui masque l’image derrière et dévoile les cases au clic, centré
 */
@Composable
fun BoardGrid(modifier: Modifier = Modifier) {
    // 25 cases : true = masquée, false = révélée
    val caseMasquee = remember { mutableStateListOf(*Array(25) { true }) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.size(400.dp)
        ) {
            repeat(5) { row ->
                Row(modifier = Modifier.weight(1f)) {
                    repeat(5) { col ->
                        val index = row * 5 + col
                        if (caseMasquee[index]) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable {
                                        caseMasquee[index] = false
                                    },
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
