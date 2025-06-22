package fr.uge.wordrawid.multi

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import fr.uge.wordrawid.dto.http.BonusMalusMoveRequest
import fr.uge.wordrawid.dto.http.CreateLobbyRequest
import fr.uge.wordrawid.dto.http.CreateLobbyResponse
import fr.uge.wordrawid.dto.http.DestroyLobbyRequest
import fr.uge.wordrawid.dto.http.JoinLobbyRequest
import fr.uge.wordrawid.dto.http.JoinLobbyResponse
import fr.uge.wordrawid.dto.http.LeaveLobbyRequest
import fr.uge.wordrawid.dto.http.RollDiceRequest
import fr.uge.wordrawid.dto.http.RollDiceResponse
import fr.uge.wordrawid.dto.http.StartGameRequest
import fr.uge.wordrawid.model.CellType
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

const val SERVER_URL = "http://10.0.2.2:8080"

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
    urlString = "$SERVER_URL/api/lobby/create",
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
      urlString = "$SERVER_URL/api/lobby/destroy",
      body = DestroyLobbyRequest(lobbyId = gameId),
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
    urlString = "$SERVER_URL/api/lobby/join",
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
      urlString ="$SERVER_URL/api/lobby/leave",
      body = LeaveLobbyRequest(lobbyId = gameId, playerId = playerId),
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
      urlString = "$SERVER_URL/api/lobby/start",
      body = StartGameRequest(lobbyId = gameId, admin = admin),
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
      val url = URL("$SERVER_URL$imageUrl")
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

fun rollDice(lobbyId: Long, playerId: Long): RollDiceResponse? {
  return postHttpRequestWithResponse(
    urlString = "$SERVER_URL/api/lobby/create",
    body = RollDiceRequest(lobbyId = lobbyId, playerId = playerId),
    logTag = "RollDice"
  )
}

fun bonusMalusMove(
  scope: CoroutineScope,
  snackbarHostState: SnackbarHostState,
  cellType: CellType,
  lobbyId: Long,
  playerId: Long,
) {
  scope.launch(Dispatchers.IO) {
    val code = postHttpRequestNoResponse(
      urlString = "$SERVER_URL/api/play/bonus-malus",
      body = BonusMalusMoveRequest(cellType, lobbyId, playerId),
      logTag = "BonusMalusMove"
    )
    withContext(Dispatchers.Main) {
      snackbarHostState.showSnackbar(
        if (code in 200..299) "Déplacement spécial effectué avec succès"
        else "Erreur serveur : $code"
      )
    }
  }
}
