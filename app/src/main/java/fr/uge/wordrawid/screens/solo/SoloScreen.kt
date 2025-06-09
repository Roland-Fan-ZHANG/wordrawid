package fr.uge.wordrawid.screens.solo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fr.uge.wordrawid.navigation.Routes

@Composable
fun SoloScreen(navController: NavController, viewModel: SoloViewModel = viewModel()) {
    val scope = rememberCoroutineScope()

    val navBackStackEntry = remember { navController.currentBackStackEntry }
    val minigameResult = navBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("minigameResult")
    minigameResult?.observe(navBackStackEntry) { result ->
        handleMinigameResult(result, viewModel)
        navBackStackEntry.savedStateHandle.remove<Boolean>("minigameResult")
    }

    if (viewModel.hasWon) {
        LaunchedEffect(Unit) { navController.navigate(Routes.WIN) }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        maxWidth

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(50.dp))

                BoardSection(
                    viewModel
                )

                Spacer(modifier = Modifier.height(20.dp))

                DiceSection(
                    viewModel,
                    onRoll = { startRolling(viewModel, scope, navController) }
                )

            }

            Text(
                text = viewModel.currentActionText,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = viewModel.gameMessage,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(4.dp)
            )

            GuessSection(
                viewModel = viewModel,
                onCheck = { checkGuess(viewModel) }
            )

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}