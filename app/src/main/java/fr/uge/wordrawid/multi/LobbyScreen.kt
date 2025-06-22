package fr.uge.wordrawid.multi

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import fr.uge.wordrawid.dto.ws.GameMessage
import fr.uge.wordrawid.dto.ws.LobbyMessage
import fr.uge.wordrawid.dto.ws.LobbyMessageType
import fr.uge.wordrawid.navigation.LoadingScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun LobbyScreen(
  gameId: Long,
  joinCode: String,
  isAdmin: Boolean,
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val secondarySnackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val players = StompClientManager.players
  val viewModel: LobbyViewModel = viewModel()
  val isLoadingGameStart by viewModel.isLoadingGameStart.collectAsState()
  StompClientManager.onLobbyMessageReceived = { lobbyMessage ->
    viewModel.onLobbyMessage(lobbyMessage)
  }
  StompClientManager.onGameMessageReceived = { gameMessage ->
    viewModel.onGameMessage(gameMessage)
  }

  Box(modifier = Modifier.fillMaxSize()) {
    if (isAdmin) {
      LaunchedEffect(Unit) {
        scope.launch {
          snackbarHostState.showSnackbar("Tu es l’administrateur de la partie")
          secondarySnackbarHostState.showSnackbar("Partie créée avec succès")
        }
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
            snackbar = {
              Snackbar {
                Text(
                  it.visuals.message,
                  modifier = Modifier.fillMaxWidth(),
                  textAlign = TextAlign.Center
                )
              }
            }
          )
          SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth(0.9f),
            snackbar = {
              Snackbar {
                Text(
                  it.visuals.message,
                  modifier = Modifier.fillMaxWidth(),
                  textAlign = TextAlign.Center
                )
              }
            }
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
          val admin = StompClientManager.players.firstOrNull()
          if (admin == null) {
            Log.w("LobbyScreen", "Aucun joueur trouvé – affichage écran de chargement")
            LoadingScreen()
            return@Column
          }
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
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            onClick = {
              destroyLobby(
                scope = scope,
                snackbarHostState = snackbarHostState,
                gameId = gameId
              )
            }
          ) {
            Text("Détruire la partie MWAHAHA", color = Color.White)
          }
        } else {
          Button(
            onClick = {
              val playerId = StompClientManager.currentPlayerId
              if (playerId != null) {
                leaveLobby(scope, snackbarHostState, gameId, playerId)
              } else {
                scope.launch {
                  snackbarHostState.showSnackbar("Impossible de quitter : ID joueur inconnu")
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Text("Quitter la partie")
          }
        }
      }
    }

    if (isLoadingGameStart) {
      LoadingScreen()
    }
  }
}

class LobbyViewModel : ViewModel() {
  private val _isLoadingGameStart = MutableStateFlow(false)
  val isLoadingGameStart: StateFlow<Boolean> = _isLoadingGameStart.asStateFlow()

  fun onLobbyMessage(message: LobbyMessage) {
    if (message.lobbyMessageType == LobbyMessageType.START) {
      _isLoadingGameStart.value = true
    }
  }

  fun onGameMessage(message: GameMessage) {
    _isLoadingGameStart.value = false
    Log.i("LobbyViewModel", "Partie démarrée avec succès : $message")
  }
}
