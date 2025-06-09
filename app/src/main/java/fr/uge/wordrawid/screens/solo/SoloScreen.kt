package fr.uge.wordrawid.screens.solo

import androidx.compose.foundation.layout.*
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(1f)) {
            DiceSection(viewModel, onRoll = { startRolling(viewModel, scope, navController) })

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                BoardSection(viewModel)

                Spacer(modifier = Modifier.height(200.dp))

                Text(
                    text = viewModel.currentActionText,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(8.dp)
                )

                Text(
                    text = viewModel.gameMessage,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }

        GuessSection(
            viewModel = viewModel,
            onCheck = { checkGuess(viewModel) }
        )

        Spacer(modifier = Modifier.height(48.dp))

    }
}
