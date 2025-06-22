package fr.uge.wordrawid.multi

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import fr.uge.wordrawid.dto.ws.GameMessage
import fr.uge.wordrawid.dto.ws.LobbyMessage
import fr.uge.wordrawid.dto.ws.LobbyMessageType
import fr.uge.wordrawid.model.Player
import fr.uge.wordrawid.navigation.Routes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.serialization.json.Json
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage
import java.io.File
import kotlin.random.Random

private fun showNotification(context: Context, title: String, message: String) {
  Log.i("NOTIF", "📣 showNotification: $message")
  val channelId = "lobby_channel"
  val channelName = "Notifications du lobby"
  val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val channel = NotificationChannel(
      channelId, channelName, NotificationManager.IMPORTANCE_HIGH
    )
    notificationManager.createNotificationChannel(channel)
  }
  val notification = NotificationCompat.Builder(context, channelId)
    .setSmallIcon(android.R.drawable.ic_dialog_info) // Remplace par ton icône si besoin
    .setContentTitle(title)
    .setContentText(message)
    .setAutoCancel(true)
    .build()
  notificationManager.notify(Random.nextInt(), notification)
}

object StompClientManager {
  private const val TAG = "STOMP"
  private lateinit var multiViewModel: MultiViewModel
  private lateinit var appContext: Context
  private var stompClient: StompClient? = null
  private val disposables = CompositeDisposable()
  private var gameImageFile: File? = null
  val players = mutableStateListOf<Player>()
  var currentPlayerId: Long? = null
  var onLobbyMessageReceived: ((LobbyMessage) -> Unit)? = null
  var onGameMessageReceived: ((GameMessage) -> Unit)? = null

  fun initialize(context: Context) {
    appContext = context.applicationContext
  }

  fun initViewModel(viewModel: MultiViewModel) {
    multiViewModel = viewModel
  }

  @SuppressLint("CheckResult")
  fun connect(joinCode: String, playerId: String, navController: NavController) {
    val wsUrl = "ws://10.0.2.2:8080/ws?playerId=$playerId"
    stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl)
    currentPlayerId = playerId.toLong()
    Log.d(TAG, "🧠 Joueur courant ID = $playerId")
    stompClient?.lifecycle()
      ?.observeOn(AndroidSchedulers.mainThread())
      ?.subscribe({ lifecycleEvent ->
        when (lifecycleEvent.type) {
          LifecycleEvent.Type.OPENED -> {
            Log.d(TAG, "✅ STOMP connecté")
            subscribeToLobby(joinCode, navController)
          }
          LifecycleEvent.Type.ERROR -> {
            Log.e(TAG, "💥 Erreur STOMP", lifecycleEvent.exception)
          }
          LifecycleEvent.Type.CLOSED -> {
            Log.d(TAG, "❌ STOMP fermé")
          }
          else -> {}
        }
      }, { e ->
        Log.e(TAG, "💥 Erreur lifecycle STOMP", e)
      })
    stompClient?.connect()
  }

  private fun subscribeToLobby(joinCode: String, navController: NavController) {
    val topic = "/topic/lobby/$joinCode"

    val disposable = stompClient?.topic(topic)
      ?.observeOn(AndroidSchedulers.mainThread())
      ?.subscribe({ msg: StompMessage ->
        Log.d(TAG, "📨 Message Lobby reçu: ${msg.payload}")
        try {
          val data = Json.decodeFromString<LobbyMessage>(msg.payload)
          onLobbyMessageReceived?.invoke(data)
          val isSelf = data.player.id == currentPlayerId
          when (data.lobbyMessageType) {
            LobbyMessageType.JOIN -> {
              if (!players.any { it.id == data.player.id }) {
                players.add(data.player)
              }
              if (!isSelf) {
                showNotification(appContext, "Nouveau joueur", data.content)
              }
            }
            LobbyMessageType.START -> {
              val gameId = data.content.toLongOrNull()
              if (gameId != null) {
                val playerName = data.player.name
                val message = "$playerName a démarré la partie dans le lobby n°$gameId"
                Log.i(TAG, "🎮 STARTED reçu. Souscription à /topic/game/$gameId")
                if (!isSelf) {
                  showNotification(appContext, "C'est PARTI !", message)
                }
                subscribeToGame(gameId, navController)
              }
            }
            LobbyMessageType.DESTROY -> {
              Log.w(TAG, "🧨 Partie détruite par ${data.player.name}")
              val message = "${data.player.name} a détruit la partie"
              if (!isSelf) {
                showNotification(appContext, "Partie annulée",  message)
              }
              disconnect()
              navController.navigate(Routes.MULTI)
            }
            LobbyMessageType.LEAVE -> {
              Log.i(TAG, "👋 ${data.player.name} a quitté le lobby")
              val toRemove = players.find { it.id == data.player.id }
              if (toRemove != null) {
                players.remove(toRemove)
                if (!isSelf) {
                  showNotification(appContext, "Départ", "${data.player.name} a quitté la partie")
                }
              }
              if (isSelf) {
                Log.d(TAG, "🚪 Redirection car joueur courant a quitté")
                disconnect()
                navController.navigate(Routes.MULTI)
              }
            }
          }
        } catch (e: Exception) {
          Log.e(TAG, "❌ Erreur de parsing STOMP. Payload: ${msg.payload}", e)
        }
      }, { e ->
        Log.e(TAG, "💥 Erreur abonnement topic", e)
      })
    disposable?.let { disposables.add(it) }
  }

  private fun subscribeToGame(gameId: Long, navController: NavController) {
    val topic = "/topic/game/$gameId"
    val disposable = stompClient?.topic(topic)
      ?.observeOn(AndroidSchedulers.mainThread())
      ?.subscribe({ msg: StompMessage ->
        Log.d(TAG, "📨 Message de jeu reçu: ${msg.payload}")
        try {
          val data = Json.decodeFromString<GameMessage>(msg.payload)
          onGameMessageReceived?.invoke(data)
          Log.i(TAG, "🎯 Données de jeu reçues pour gameId=${data.lobby.id}")
          downloadImage(appContext, data.imageUrl, data.lobby.id) { file ->
            gameImageFile = file
            multiViewModel.currentGameData = data.lobby
            navController.navigate("game/${data.lobby.id}")
          }
        } catch (e: Exception) {
          Log.e(TAG, "❌ Erreur parsing GameMessage", e)
        }
      }, { e ->
        Log.e(TAG, "💥 Erreur abonnement topic /game/$gameId", e)
      })

    disposable?.let { disposables.add(it) }
  }

  private fun disconnect() {
    stompClient?.disconnect()
    stompClient = null
    players.clear()
    currentPlayerId = -1
    disposables.clear()
    Log.d(TAG, "🔌 STOMP déconnecté et nettoyé")
  }
}
