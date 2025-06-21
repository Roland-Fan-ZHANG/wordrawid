package fr.uge.wordrawid.multi

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import fr.uge.wordrawid.dto.http.CreateLobbyRequest
import fr.uge.wordrawid.dto.http.CreateLobbyResponse
import fr.uge.wordrawid.dto.http.DestroyLobbyRequest
import fr.uge.wordrawid.dto.http.JoinLobbyRequest
import fr.uge.wordrawid.dto.http.JoinLobbyResponse
import fr.uge.wordrawid.dto.http.LeaveLobbyRequest
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

inline fun <reified Req, reified Res> postHttpRequestWithResponse(
  urlString: String,
  body: Req,
  logTag: String = "HTTP",
): Res? {
  return try {
    val url = URL(urlString)
    val connection = url.openConnection() as HttpURLConnection
    val json = Json { ignoreUnknownKeys = true }
    val jsonBody = json.encodeToString(body)
    connection.apply {
      requestMethod = "POST"
      doOutput = true
      setRequestProperty("Content-Type", "application/json")
      outputStream.write(jsonBody.toByteArray(Charsets.UTF_8))
    }
    val responseCode = connection.responseCode
    if (responseCode in 200..299) {
      val responseText = connection.inputStream.bufferedReader().readText()
      json.decodeFromString(responseText)
    } else {
      Log.e(logTag, "Erreur HTTP $responseCode")
      null
    }
  } catch (e: Exception) {
    Log.e(logTag, "Exception HTTP", e)
    null
  }
}

inline fun <reified Req> postHttpRequestNoResponse(
  urlString: String,
  body: Req,
  logTag: String = "HTTP"
): Int {
  return try {
    val url = URL(urlString)
    val connection = url.openConnection() as HttpURLConnection
    val json = Json.encodeToString(body)
    connection.apply {
      requestMethod = "POST"
      doOutput = true
      setRequestProperty("Content-Type", "application/json")
      outputStream.write(json.toByteArray(Charsets.UTF_8))
    }
    connection.responseCode
  } catch (e: Exception) {
    Log.e(logTag, "Exception HTTP", e)
    -1
  }
}

fun createLobby(pseudo: String): CreateLobbyResponse? {
  return postHttpRequestWithResponse(
    urlString = "http://10.0.2.2:8080/api/lobby/create",
    body = CreateLobbyRequest(pseudo),
    logTag = "CreateGameScreen"
  )
}

fun destroyLobby(
  scope: CoroutineScope,
  snackbarHostState: SnackbarHostState,
  gameId: Long,
) {
  scope.launch(Dispatchers.IO) {
    val code = postHttpRequestNoResponse(
      urlString = "http://10.0.2.2:8080/api/lobby/destroy",
      body = DestroyLobbyRequest(sessionId = gameId),
      logTag = "DestroyLobby"
    )
    withContext(Dispatchers.Main) {
      snackbarHostState.showSnackbar(
        if (code in 200..299) "Partie détruite avec succès"
        else "Erreur serveur : $code"
      )
    }
  }
}

fun joinLobby(pseudo: String, joinCode: String): JoinLobbyResponse? {
  return postHttpRequestWithResponse(
    urlString = "http://10.0.2.2:8080/api/lobby/join",
    body = JoinLobbyRequest(pseudo, joinCode),
    logTag = "JoinGameScreen"
  )
}

fun leaveLobby(
  scope: CoroutineScope,
  snackbarHostState: SnackbarHostState,
  gameId: Long,
  playerId: Long,
) {
  scope.launch(Dispatchers.IO) {
    val code = postHttpRequestNoResponse(
      urlString = "http://10.0.2.2:8080/api/lobby/leave",
      body = LeaveLobbyRequest(sessionId = gameId, playerId = playerId),
      logTag = "LeaveLobby"
    )
    withContext(Dispatchers.Main) {
      snackbarHostState.showSnackbar(
        if (code in 200..299) "Partie quittée avec succès"
        else "Erreur serveur : $code"
      )
    }
  }
}

fun startGame(
  scope: CoroutineScope,
  snackbarHostState: SnackbarHostState,
  admin: Player,
  gameId: Long,
) {
  scope.launch(Dispatchers.IO) {
    val code = postHttpRequestNoResponse(
      urlString = "http://10.0.2.2:8080/api/lobby/start",
      body = StartGameRequest(gameId = gameId, admin = admin),
      logTag = "LeaveLobby"
    )
    withContext(Dispatchers.Main) {
      snackbarHostState.showSnackbar(
        if (code in 200..299) "Partie démarrée avec succès"
        else "Erreur serveur : $code"
      )
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
