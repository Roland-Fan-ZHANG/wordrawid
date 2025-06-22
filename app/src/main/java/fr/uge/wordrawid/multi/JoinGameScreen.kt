package fr.uge.wordrawid.multi

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.wordrawid.navigation.LoadingScreen
import kotlinx.coroutines.*

@Composable
fun JoinGameScreen(navController: NavController) {
  var pseudo by remember { mutableStateOf("") }
  var joinCode by remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }
  var responseMessage by remember { mutableStateOf<String?>(null) }
  val isFormValid = pseudo.isNotBlank() && joinCode.isNotBlank()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text("Rejoindre une partie", style = MaterialTheme.typography.headlineMedium)
    Spacer(modifier = Modifier.height(32.dp))
    OutlinedTextField(
      value = pseudo,
      onValueChange = { pseudo = it },
      label = { Text("Votre pseudo") },
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
      value = joinCode,
      onValueChange = { joinCode = it },
      label = { Text("Code de la partie") },
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(24.dp))
    Button(
      onClick = {
        if (!isFormValid) {
          responseMessage = "Veuillez remplir correctement le pseudo et le code."
          return@Button
        }
        isLoading = true
        responseMessage = null
        CoroutineScope(Dispatchers.IO).launch {
          Log.i("JoinGameScreen", "Pseudo = $pseudo ; Joincode = $joinCode")
          val result = joinLobby(pseudo = pseudo, joinCode = joinCode)
          withContext(Dispatchers.Main) {
            isLoading = false
            if (result != null) {
              StompClientManager.players.clear()
              StompClientManager.players.addAll(result.otherPlayers)
              StompClientManager.players.add(result.player)
                StompClientManager.connect(
                    result.joinCode,
                    result.player.id.toString(),
                    navController
                )
              navController.navigate("lobby/${result.lobbyId}?joinCode=${result.joinCode}&isAdmin=false")
            } else {
              responseMessage = "Erreur lors de la tentative de connexion. Vérifiez les infos."
            }
          }
        }
      },
      enabled = isFormValid,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Rejoindre la partie")
    }
    Spacer(modifier = Modifier.height(16.dp))

    if (isLoading) {
      LoadingScreen()
    }
    responseMessage?.let {
      Text(it, modifier = Modifier.padding(top = 16.dp), color = MaterialTheme.colorScheme.error)
    }
  }
}
