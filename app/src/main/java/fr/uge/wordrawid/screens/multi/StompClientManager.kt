package fr.uge.wordrawid.screens.multi

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.NotificationCompat
import fr.uge.wordrawid.model.Player
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage
import kotlin.random.Random

@Serializable
enum class NotificationType {
  JOIN, LEAVE, STARTED
}

@Serializable
data class LobbyMessage(
  val notificationType: NotificationType,
  val player: Player,
  val content: String
)

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
  val players = mutableStateListOf<Player>()
  var currentPlayerId: Long? = null

  fun initialize(context: Context) {
    appContext = context.applicationContext
  }

  @SuppressLint("CheckResult")
  fun connect(joinCode: String, playerId: String) {
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
            subscribeToLobby(joinCode)
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

  private fun subscribeToLobby(joinCode: String) {
    val topic = "/topic/lobby/$joinCode"
    val disposable = stompClient?.topic(topic)
      ?.observeOn(AndroidSchedulers.mainThread())
      ?.subscribe({ msg: StompMessage ->
        Log.d(TAG, "📨 Message reçu: ${msg.payload}")
        try {
          val data = Json.decodeFromString<LobbyMessage>(msg.payload)
          if (data.notificationType == NotificationType.JOIN) {
            val isSelf = data.player.id == currentPlayerId
            if (!players.any { it.id == data.player.id }) {
              players.add(data.player)
              Log.i(TAG, "👥 Nouvelle liste des joueurs (${players.size}) : " +
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
        } catch (e: Exception) {
          Log.e(TAG, "❌ Erreur de parsing STOMP", e)
        }
      }, { e ->
        Log.e(TAG, "💥 Erreur abonnement topic", e)
      })

    disposable?.let { disposables.add(it) }
  }

  fun disconnect() {
    disposables.clear()
    stompClient?.disconnect()
  }
}
