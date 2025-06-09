package fr.uge.wordrawid.screens.solo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.uge.wordrawid.R

/**
 * Affiche une image aléatoire dans un carré
 */
@Composable
fun RandomImage(modifier: Modifier = Modifier) {
    val images = listOf(
        R.drawable.pomme,
        R.drawable.voiture,
        R.drawable.maison,
        R.drawable.imprimante,
        R.drawable.chat
    )
    val randomImage = remember { images.random() }
    Image(
        painter = painterResource(id = randomImage),
        contentDescription = "Image aléatoire",
        modifier = modifier.size(400.dp),
        contentScale = ContentScale.Crop
    )
}
