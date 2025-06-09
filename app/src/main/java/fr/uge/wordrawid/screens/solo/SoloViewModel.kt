package fr.uge.wordrawid.screens.solo

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import fr.uge.wordrawid.screens.solo.images.imagesEtMots

class SoloViewModel : ViewModel() {
    private val randomPair = imagesEtMots.random()
    val randomImageRes = randomPair.first
    val motADeviner = randomPair.second

    var finalResult by mutableIntStateOf(1)
    var displayResult by mutableIntStateOf(1)
    var rolling by mutableStateOf(false)
    var playerPosition by mutableIntStateOf(0)
    var currentActionText by mutableStateOf("")
    var guess by mutableStateOf("")
    var gameMessage by mutableStateOf("")
    var hasWon by mutableStateOf(false)

    val caseMasquee = SnapshotStateList<Boolean>().apply {
        addAll(List(25) { true })
    }
}
