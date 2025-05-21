package fr.uge.wordrawid.screens.multi

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.wordrawid.navigation.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class CreateLobbyRequest(val pseudo: String)

@Composable
fun CreateGameScreen(navController: NavController) {
  var pseudo by remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }
  var responseMessage by remember { mutableStateOf<String?>(null) }

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
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
          val result = createLobbyRequest(pseudo)
          withContext(Dispatchers.Main) {
            isLoading = false
            responseMessage = result

            if (result == "Partie créée avec succès") {
              navController.navigate(Routes.LOBBY)
            }
          }
        }
      },
      enabled = pseudo.isNotBlank(),
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Créer la partie")
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (isLoading) {
      CircularProgressIndicator()
    }
    responseMessage?.let {
      Text(it, modifier = Modifier.padding(top = 16.dp))
    }
  }
}

fun createLobbyRequest(pseudo: String): String {
  return try {
    val url = URL("http://10.0.2.2:8080/api/lobby/create")
    val json = Json { ignoreUnknownKeys = true }
    val jsonBody = json.encodeToString(CreateLobbyRequest(pseudo))
    val connection = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "POST"
      doOutput = true
      setRequestProperty("Content-Type", "application/json")
      outputStream.write(jsonBody.toByteArray())
    }
    val responseCode = connection.responseCode
    if (responseCode in 200..299) {
      "Partie créée avec succès"
    } else {
      "Erreur serveur : $responseCode"
    }
  } catch (e: Exception) {
    "Erreur : ${e.message}"
  }
}
