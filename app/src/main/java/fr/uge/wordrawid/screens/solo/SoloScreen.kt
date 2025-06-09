package fr.uge.wordrawid.screens.solo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fr.uge.wordrawid.navigation.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SoloScreen(navController: NavController, viewModel: SoloViewModel = viewModel()) {
    val scope = rememberCoroutineScope()

    val navBackStackEntry = remember { navController.currentBackStackEntry }
    val minigameResult = navBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("minigameResult")
    minigameResult?.observe(navBackStackEntry) { result ->
        if (result == true) {
            viewModel.caseMasquee[viewModel.playerPosition] = false
            viewModel.currentActionText = "Case révélée"
        } else {
            viewModel.currentActionText = "Mini-jeu échoué, la case est bloquée"
        }
        navBackStackEntry.savedStateHandle.remove<Boolean>("minigameResult")
    }

    fun animateMovement(steps: Int, forward: Boolean = true) = scope.launch {
        repeat(steps) {
            viewModel.playerPosition = if (forward)
                (viewModel.playerPosition + 1) % 25
            else
                (viewModel.playerPosition - 1 + 25) % 25
            delay(200)
        }
    }

    fun handleAction(action: CaseAction) = scope.launch {
        viewModel.currentActionText = when (action) {
            is CaseAction.MoveForward2 -> "Avance de 2 cases!"
            is CaseAction.MoveBackward3 -> "Recule de 3 cases!"
            is CaseAction.CompassMiniGame -> "Mini-jeu !"
            is CaseAction.RevealTile -> "Révèle une case!"
            is CaseAction.Nothing -> "Aucune action."
        }

        when (action) {
            is CaseAction.MoveForward2 -> animateMovement(2)
            is CaseAction.MoveBackward3 -> animateMovement(3, forward = false)
            is CaseAction.RevealTile -> viewModel.caseMasquee[viewModel.playerPosition] = false
            is CaseAction.CompassMiniGame -> navController.navigate(Routes.COMPASS)
            is CaseAction.Nothing -> {}
        }
    }

    fun startRolling() {
        if (viewModel.rolling) return
        viewModel.rolling = true
        scope.launch {
            repeat(10) {
                viewModel.displayResult = (1..6).random()
                delay(50)
            }
            viewModel.finalResult = rollDice()
            viewModel.displayResult = viewModel.finalResult

            animateMovement(viewModel.finalResult).join()
            handleAction(boardActions[viewModel.playerPosition])
            viewModel.rolling = false
        }
    }

    fun checkGuess() {
        if (viewModel.guess.trim().lowercase() == viewModel.motADeviner.lowercase()) {
            viewModel.hasWon = true
        } else {
            viewModel.gameMessage = "Raté !"
        }
    }

    if (viewModel.hasWon) {
        LaunchedEffect(Unit) {
            navController.navigate(Routes.WIN)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DiceWithImage(
            displayResult = viewModel.displayResult,
            onRoll = { startRolling() },
            rolling = viewModel.rolling
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewModel.gameMessage,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            Text(
                text = viewModel.currentActionText,
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
                    painter = painterResource(id = viewModel.randomImageRes),
                    contentDescription = null,
                    modifier = Modifier.size(400.dp)
                )
                BoardGrid(viewModel.caseMasquee)
                Player(position = viewModel.playerPosition)
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = viewModel.guess,
                onValueChange = { viewModel.guess = it },
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
    }
}
