package fr.uge.wordrawid.multi

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import fr.uge.wordrawid.dto.http.CreateLobbyRequest
import fr.uge.wordrawid.dto.http.CreateLobbyResponse
import fr.uge.wordrawid.dto.http.DestroyLobbyRequest
import fr.uge.wordrawid.dto.http.JoinLobbyRequest
import fr.uge.wordrawid.dto.http.JoinLobbyResponse
import fr.uge.wordrawid.dto.http.LeaveSessionRequest
import fr.uge.wordrawid.dto.http.StartGameRequest
import fr.uge.wordrawid.model.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

fun createLobby(pseudo: String): CreateLobbyResponse? {
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

    if (connection.responseCode in 200..299) {
      val response = connection.inputStream.bufferedReader().readText()
      json.decodeFromString<CreateLobbyResponse>(response)
    } else {
      Log.e("CreateGameScreen", "Erreur lors de la création de la partie : ${connection.responseCode}")
      null
    }
  } catch (e: Exception) {
    Log.e("CreateGameScreen", "Erreur lors de la création de la partie", e)
    null
  }
}

fun destroyLobby(
  scope: CoroutineScope,
  snackbarHostState: SnackbarHostState,
  gameId: Long,
) {
  scope.launch(Dispatchers.IO) {
    try {
      val url = URL("http://10.0.2.2:8080/api/lobby/destroy")
      val connection = url.openConnection() as HttpURLConnection
      connection.requestMethod = "POST"
      connection.doOutput = true
      connection.setRequestProperty("Content-Type", "application/json")

      val request = DestroyLobbyRequest(sessionId = gameId)
      val json = Json.encodeToString(request)

      connection.outputStream.use { output ->
        output.write(json.toByteArray(Charsets.UTF_8))
      }

      val code = connection.responseCode
      withContext(Dispatchers.Main) {
        if (code in 200..299) {
          snackbarHostState.showSnackbar("Partie détruite avec succès")
        } else {
          snackbarHostState.showSnackbar("Erreur serveur : $code")
        }
      }
    } catch (e: Exception) {
      withContext(Dispatchers.Main) {
        snackbarHostState.showSnackbar("Erreur : ${e.message}")
      }
    }
  }
}

fun joinLobby(pseudo: String, joinCode: String): JoinLobbyResponse? {
  return try {
    val url = URL("http://10.0.2.2:8080/api/lobby/join")
    val json = Json { ignoreUnknownKeys = true }
    val jsonBody = json.encodeToString(JoinLobbyRequest(pseudo = pseudo, joinCode = joinCode))
    val connection = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "POST"
      doOutput = true
      setRequestProperty("Content-Type", "application/json")
      outputStream.write(jsonBody.toByteArray())
    }

    if (connection.responseCode in 200..299) {
      val response = connection.inputStream.bufferedReader().readText()
      json.decodeFromString<JoinLobbyResponse>(response)
    } else {
      Log.e("JoinGameScreen", "Erreur HTTP ${connection.responseCode}")
      null
    }
  } catch (e: Exception) {
    Log.e("JoinGameScreen", "Erreur lors de la requête de join", e)
    null
  }
}

fun leaveLobby(
  scope: CoroutineScope,
  snackbarHostState: SnackbarHostState,
  gameId: Long,
  playerId: Long,
) {
  scope.launch(Dispatchers.IO) {
    try {
      val url = URL("http://10.0.2.2:8080/api/lobby/leave")
      val connection = url.openConnection() as HttpURLConnection
      connection.requestMethod = "POST"
      connection.doOutput = true
      connection.setRequestProperty("Content-Type", "application/json")

      val request = LeaveSessionRequest(sessionId = gameId, playerId = playerId)
      val json = Json.encodeToString(request)

      connection.outputStream.use { output ->
        output.write(json.toByteArray(Charsets.UTF_8))
      }

      val code = connection.responseCode
      withContext(Dispatchers.Main) {
        if (code in 200..299) {
          snackbarHostState.showSnackbar("Vous avez quitté la partie")
        } else {
          snackbarHostState.showSnackbar("Erreur serveur : $code")
        }
      }
    } catch (e: Exception) {
      withContext(Dispatchers.Main) {
        snackbarHostState.showSnackbar("Erreur : ${e.message}")
      }
    }
  }
}

fun startGame(
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

fun downloadImage(context: Context, imageUrl: String, gameId: Long, onDownloaded: (File) -> Unit) {
  CoroutineScope(Dispatchers.IO).launch {
    try {
      val url = URL("http://10.0.2.2:8080$imageUrl")
      Log.i("imageUrl", imageUrl)
      val connection = url.openConnection() as HttpURLConnection
      connection.requestMethod = "GET"

      val inputStream = connection.inputStream
      val file = File(context.cacheDir, "image_$gameId.jpg")
      file.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
      }

      withContext(Dispatchers.Main) {
        onDownloaded(file)
      }
    } catch (e: Exception) {
      Log.e("ImageDownload", "Erreur téléchargement image", e)
    }
  }
}
