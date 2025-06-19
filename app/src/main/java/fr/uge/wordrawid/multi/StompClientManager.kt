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
import fr.uge.wordrawid.model.Session
import fr.uge.wordrawid.model.Player
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

private fun showNotification(context: Context, message: String) {
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
    .setContentTitle("Nouveau joueur")
    .setContentText(message)
    .setAutoCancel(true)
    .build()

  notificationManager.notify(Random.nextInt(), notification)
}

object StompClientManager {
  private const val TAG = "STOMP"
  private lateinit var appContext: Context
  private var stompClient: StompClient? = null
  private val disposables = CompositeDisposable()
  private var latestSession: Session? = null
  private var gameImageFile: File? = null
  val players = mutableStateListOf<Player>()
  private var currentPlayerId: Long? = null

  fun initialize(context: Context) {
    appContext = context.applicationContext
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
        Log.d(TAG, "📨 Message reçu: ${msg.payload}")

        try {
          // 🔍 Log brut avant parsing
          Log.d(TAG, "🔎 Tentative de parse JSON en LobbyMessage")

          val data = Json.decodeFromString<LobbyMessage>(msg.payload)

          when (data.lobbyMessageType) {
            LobbyMessageType.JOIN -> {
              val isSelf = data.player.id == currentPlayerId
              if (!players.any { it.id == data.player.id }) {
                players.add(data.player)
                Log.i(
                    TAG, "👥 Nouvelle liste des joueurs (${players.size}) : " +
                        players.joinToString { "${it.id}-${it.name}" })
              }
              if (isSelf) {
                Log.d(TAG, "🟢 C’est moi, pas de notif standard")
              } else {
                Log.i(TAG, "🔔 Notification pour ${data.player.name}")
                Log.d(TAG, "🔔 Affichage notif pour ${data.player.name} sur thread: ${Thread.currentThread().name}")
                showNotification(appContext, data.content)
              }
            }

            LobbyMessageType.START -> {
              val gameId = data.content.toLongOrNull()
              if (gameId != null) {
                val playerName = data.player.name
                val message = "$playerName a démarré la partie dans le lobby n°$gameId"

                Log.i(TAG, "🎮 STARTED reçu. Souscription à /topic/game/$gameId")
                showNotification(appContext, message)
                subscribeToGame(gameId, navController)
              } else {
                Log.e(TAG, "❌ STARTED reçu avec content non convertible en Long : ${data.content}")
              }
            }

            else -> Log.w(TAG, "⚠️ Notification inconnue ou non gérée : ${data.lobbyMessageType}")
          }

        } catch (e: Exception) {
          Log.e(TAG, "❌ Erreur de parsing STOMP", e)
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
          Log.i(TAG, "🎯 Données de jeu reçues pour gameId=${data.session.id}")
          Log.i(TAG, "📋 Session: ${data.session}")
          Log.i(TAG, "📋 Joueurs: ${data.session.players.joinToString { it.name }}")
          Log.i(TAG, "📋 Plateau: ${data.session.gameManager.board.size} cases")
          Log.i(TAG, "📋 Image URL: ${data.imageUrl}")

          downloadImage(appContext, data.imageUrl, data.session.id) { file ->
            gameImageFile = file
            latestSession = data.session
            navController.navigate("game/${data.session.id}")
          }
        } catch (e: Exception) {
          Log.e(TAG, "❌ Erreur parsing GameMessage", e)
        }
      }, { e ->
        Log.e(TAG, "💥 Erreur abonnement topic /game/$gameId", e)
      })

    disposable?.let { disposables.add(it) }
  }


  fun disconnect() {
    disposables.clear()
    stompClient?.disconnect()
  }
}

private fun downloadImage(context: Context, imageUrl: String, gameId: Long, onDownloaded: (File) -> Unit) {
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
