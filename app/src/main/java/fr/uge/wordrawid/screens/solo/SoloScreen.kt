package fr.uge.wordrawid.screens.solo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import fr.uge.wordrawid.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SoloScreen() {
    val imagesEtMots = listOf(
        R.drawable.image1 to "chat",
        R.drawable.image2 to "chien",
        R.drawable.image3 to "soleil",
        R.drawable.image4 to "lune",
        R.drawable.image5 to "arbre"
    )
    val (randomImageRes, motADeviner) = remember { imagesEtMots.random() }

    var finalResult by remember { mutableStateOf(1) }
    var displayResult by remember { mutableStateOf(1) }
    var rolling by remember { mutableStateOf(false) }
    var playerPosition by remember { mutableStateOf(0) }
    var currentActionText by remember { mutableStateOf("") }
    val caseMasquee = remember { mutableStateListOf(*Array(25) { true }) }
    var guess by remember { mutableStateOf("") }
    var gameMessage by remember { mutableStateOf("") }
    var hasWon by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun animateMovement(steps: Int, forward: Boolean = true) = scope.launch {
        repeat(steps) {
            playerPosition = if (forward) (playerPosition + 1) % 25 else (playerPosition - 1 + 25) % 25
            delay(200)
        }
    }

    fun handleAction(action: CaseAction) = scope.launch {
        currentActionText = when (action) {
            is CaseAction.MoveForward2 -> "Avance de 2 cases!"
            is CaseAction.MoveBackward2 -> "Recule de 2 cases!"
            is CaseAction.MiniGame -> "Mini-jeu à lancer!"
            is CaseAction.RevealTile -> "Révèle une case!"
            is CaseAction.Nothing -> "Aucune action."
        }

        when (action) {
            is CaseAction.MoveForward2 -> animateMovement(2)
            is CaseAction.MoveBackward2 -> animateMovement(2, forward = false)
            is CaseAction.RevealTile -> caseMasquee[playerPosition] = false
            is CaseAction.MiniGame -> {/* TODO */}
            is CaseAction.Nothing -> {}
        }
    }

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

            animateMovement(finalResult).join()
            handleAction(boardActions[playerPosition])
            rolling = false
        }
    }

    fun checkGuess() {
        if (guess.trim().lowercase() == motADeviner.lowercase()) {
            gameMessage = "Gagné !"
            hasWon = true
        } else {
            gameMessage = "Raté !"
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DiceWithImage(
            displayResult = displayResult,
            onRoll = { startRolling() },
            rolling = rolling
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentActionText,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 100.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = randomImageRes),
                    contentDescription = null,
                    modifier = Modifier.size(400.dp)
                )
                BoardGrid(caseMasquee)
                Player(position = playerPosition)
            }

            Spacer(modifier = Modifier.height(80.dp))

            if (hasWon) {
                //TODO : afficher la page de win
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = guess,
                onValueChange = { guess = it },
                label = { Text("Devine le mot") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                modifier = Modifier.weight(1f).padding(bottom = 20.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = { checkGuess() }) {
                Text("Valider")
            }
        }

        Text(
            text = gameMessage,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )
    }
}