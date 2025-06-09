package fr.uge.wordrawid.screens.solo

import androidx.navigation.NavController
import fr.uge.wordrawid.navigation.Routes
import fr.uge.wordrawid.screens.solo.board.CaseAction
import fr.uge.wordrawid.screens.solo.board.boardActions
import fr.uge.wordrawid.screens.solo.dice.rollDice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun handleMinigameResult(result: Boolean, viewModel: SoloViewModel) {
    if (result) {
        viewModel.caseMasquee[viewModel.playerPosition] = false
        viewModel.currentActionText = "Case révélée"
    } else {
        viewModel.currentActionText = "Mini-jeu échoué, la case est bloquée"
    }
}

fun animateMovement(viewModel: SoloViewModel, steps: Int, scope: CoroutineScope, forward: Boolean = true) = scope.launch {
    repeat(steps) {
        viewModel.playerPosition = if (forward)
            (viewModel.playerPosition + 1) % 25
        else
            (viewModel.playerPosition - 1 + 25) % 25
        delay(200)
    }
}

fun handleAction(viewModel: SoloViewModel, scope: CoroutineScope, navController: NavController) = scope.launch {
    val action = boardActions[viewModel.playerPosition]

    if (!viewModel.caseMasquee[viewModel.playerPosition] &&
        (action is CaseAction.CompassMiniGame || action is CaseAction.BalloonMiniGame)) {
        viewModel.currentActionText = "Case déjà révélée, pas de mini-jeu"
        return@launch
    }

    viewModel.currentActionText = when (action) {
        is CaseAction.MoveForward2 -> "Avance de 2 cases"
        is CaseAction.MoveBackward3 -> "Recule de 3 cases"
        is CaseAction.CompassMiniGame -> "Mini-jeu"
        is CaseAction.BalloonMiniGame -> "Mini-jeu"
        is CaseAction.RevealTile -> "Révèle une case"
        is CaseAction.Nothing -> "Aucune action"
    }

    when (action) {
        is CaseAction.MoveForward2 -> animateMovement(viewModel, 2, scope)
        is CaseAction.MoveBackward3 -> animateMovement(viewModel, 3, scope, forward = false)
        is CaseAction.RevealTile -> viewModel.caseMasquee[viewModel.playerPosition] = false
        is CaseAction.CompassMiniGame -> navController.navigate(Routes.COMPASS)
        is CaseAction.BalloonMiniGame -> navController.navigate(Routes.BALLOON)
        is CaseAction.Nothing -> {}
    }
}

fun startRolling(viewModel: SoloViewModel, scope: CoroutineScope, navController: NavController) {
    if (viewModel.rolling) return
    viewModel.rolling = true

    scope.launch {
        repeat(10) {
            viewModel.displayResult = (1..6).random()
            delay(50)
        }
        viewModel.finalResult = rollDice()
        viewModel.displayResult = viewModel.finalResult

        animateMovement(viewModel, viewModel.finalResult, scope).join()
        handleAction(viewModel, scope, navController)
        viewModel.rolling = false
    }
}

fun checkGuess(viewModel: SoloViewModel) {
    if (viewModel.guess.trim().lowercase() == viewModel.motADeviner.lowercase()) {
        viewModel.hasWon = true
    } else {
        viewModel.gameMessage = "Raté !"
    }
}
