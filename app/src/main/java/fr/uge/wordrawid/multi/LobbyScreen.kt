package fr.uge.wordrawid.multi

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import fr.uge.wordrawid.dto.http.StartGameRequest
import fr.uge.wordrawid.model.Player

private fun startGame(
  scope: CoroutineScope,
  snackbarHostState: SnackbarHostState,
  admin: Player,
  gameId: Long
) {
  scope.launch(Dispatchers.IO) {
    val requestBody = StartGameRequest(admin, gameId)
    val jsonBody = Json.encodeToString(requestBody)

    val url = URL("http://10.0.2.2:8080/api/lobby/start")
    val connection = url.openConnection() as HttpURLConnection
    try {
      connection.requestMethod = "POST"
      connection.setRequestProperty("Content-Type", "application/json")
      connection.doOutput = true
      connection.outputStream.use {
        it.write(jsonBody.toByteArray())
      }

      val code = connection.responseCode
      val message = if (code in 200..299) {
        "Partie démarrée avec succès"
      } else {
        "Erreur serveur : $code"
      }
      withContext(Dispatchers.Main) {
        snackbarHostState.showSnackbar(message)
      }
    } catch (e: Exception) {
      withContext(Dispatchers.Main) {
        snackbarHostState.showSnackbar("Erreur : ${e.message}")
      }
    } finally {
      connection.disconnect()
    }
  }
}

@Composable
fun LobbyScreen(
  gameId: Long,
  joinCode: String,
  isAdmin: Boolean
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val secondarySnackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val players = StompClientManager.players

  if (isAdmin) {
    LaunchedEffect(Unit) {
      scope.launch { snackbarHostState.showSnackbar("Tu es l’administrateur de la partie") }
      scope.launch { secondarySnackbarHostState.showSnackbar("Partie créée avec succès") }
    }
  } else {
    LaunchedEffect(Unit) {
      scope.launch { secondarySnackbarHostState.showSnackbar("Tu as rejoint la partie : $joinCode") }
    }
  }

  Scaffold(
    snackbarHost = {},
    bottomBar = {
      Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        SnackbarHost(
          hostState = secondarySnackbarHostState,
          modifier = Modifier.fillMaxWidth(0.9f).padding(bottom = 8.dp),
          snackbar = { Snackbar {
            Text(
              it.visuals.message,
              modifier = Modifier.fillMaxWidth(),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          } }
        )
        SnackbarHost(
          hostState = snackbarHostState,
          modifier = Modifier.fillMaxWidth(0.9f),
          snackbar = { Snackbar {
            Text(
              it.visuals.message,
              modifier = Modifier.fillMaxWidth(),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          } }
        )
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text("Code de la partie : $joinCode", style = MaterialTheme.typography.headlineMedium)
      Spacer(modifier = Modifier.height(24.dp))
      Text("Joueurs :", style = MaterialTheme.typography.titleMedium)
      Spacer(modifier = Modifier.height(12.dp))
      players.forEach { player ->
        Text("• ${player.id} - ${player.name}")
      }
      if (isAdmin) {
        val admin = StompClientManager.players.first()
        Spacer(modifier = Modifier.height(32.dp))
        Button(
          onClick = {
            startGame(
              scope = scope,
              snackbarHostState = snackbarHostState,
              admin = admin,
              gameId = gameId
            )
          }
        ) {
          Text("Démarrer la partie")
        }
      }
    }
  }
}
