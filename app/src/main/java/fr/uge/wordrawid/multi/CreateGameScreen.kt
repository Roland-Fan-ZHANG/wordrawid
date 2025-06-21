package fr.uge.wordrawid.multi

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.wordrawid.navigation.LoadingScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreateGameScreen(navController: NavController) {
  var pseudo by remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }
  var responseMessage by remember { mutableStateOf<String?>(null) }
  val isPseudoValid = pseudo.isNotBlank()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text("Créer une partie", style = MaterialTheme.typography.headlineMedium)
    Spacer(modifier = Modifier.height(32.dp))
    OutlinedTextField(
      value = pseudo,
      onValueChange = { pseudo = it },
      label = { Text("Votre pseudo") },
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(24.dp))
    Button(
      onClick = {
        if (!isPseudoValid) {
          responseMessage = "Veuillez remplir correctement le pseudo."
          return@Button
        }
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
          val result = createLobby(pseudo)
          withContext(Dispatchers.Main) {
            isLoading = false
            if (result != null) {
              StompClientManager.players.add(result.player)
                StompClientManager.connect(
                    result.joinCode,
                    result.player.id.toString(),
                    navController
                )
              navController.navigate("lobby/${result.gameId}?joinCode=${result.joinCode}&isAdmin=true")
            } else {
              responseMessage = "Erreur lors de la tentative de connexion. Vérifiez les infos."
            }
          }
        }
      },
      enabled = isPseudoValid,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Créer la partie")
    }
    Spacer(modifier = Modifier.height(16.dp))

    if (isLoading) {
      LoadingScreen()
    }
    responseMessage?.let {
      Text(it, modifier = Modifier.padding(top = 16.dp))
    }
  }
}
