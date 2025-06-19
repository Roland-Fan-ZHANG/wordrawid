package fr.uge.wordrawid.solo.board

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Plateau de jeu 5x5 avec les numéros de case affichés
 */
@Composable
fun BoardGrid(caseMasquee: SnapshotStateList<Boolean>) {
    Box(
        modifier = Modifier.size(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            repeat(5) { row ->
                Row(modifier = Modifier.weight(1f)) {
                    repeat(5) { col ->
                        val index = row * 5 + col
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (caseMasquee[index]) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color.DarkGray,
                                    border = BorderStroke(1.dp, Color.White),
                                    shape = RoundedCornerShape(0.dp)
                                ) {}
                            }
                            Text(
                                text = (index + 1).toString(),
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}