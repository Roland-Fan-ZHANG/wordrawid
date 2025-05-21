package fr.uge.wordrawid.screens.multi

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun LobbyScreen() {
  val snackbarHostState = remember { SnackbarHostState() }
  val secondarySnackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val players = remember { listOf("Toi", "Joueur 2", "Joueur 3") } // à remplacer dynamiquement
  val joinCode = remember { "ABC123" } // à remplacer dynamiquement

  // Lancer les deux snackbars dès l'arrivée sur l'écran
  LaunchedEffect(Unit) {
    scope.launch { snackbarHostState.showSnackbar("Tu es l’administrateur de la partie") }
    scope.launch { secondarySnackbarHostState.showSnackbar("Partie créée avec succès") }
  }

  Scaffold(
    snackbarHost = {},

    bottomBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        SnackbarHost(
          hostState = secondarySnackbarHostState,
          modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 8.dp),
          snackbar = { Snackbar { Text(
            it.visuals.message,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          ) } }
        )

        SnackbarHost(
          hostState = snackbarHostState,
          modifier = Modifier
            .fillMaxWidth(0.9f),
          snackbar = {
            Snackbar {
              Text(
                it.visuals.message,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        )
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .padding(padding)
        .fillMaxSize()
        .padding(24.dp),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text("Code de la partie : $joinCode", style = MaterialTheme.typography.headlineMedium)

      Spacer(modifier = Modifier.height(24.dp))

      Text("Joueurs :", style = MaterialTheme.typography.titleMedium)

      Spacer(modifier = Modifier.height(12.dp))

      players.forEach { player ->
        Text("• $player")
      }
    }
  }
}
