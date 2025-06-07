package fr.uge.wordrawid.screens.multi

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import fr.uge.wordrawid.model.Player
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage

@Serializable
data class LobbyMessage(val type: String, val player: Player)

object StompClientManager {
  private const val TAG = "STOMP"
  private var stompClient: StompClient? = null
  private val disposables = CompositeDisposable()
  val players = mutableStateListOf<Player>()

  @SuppressLint("CheckResult")
  fun connect(joinCode: String, playerId: String) {
    val wsUrl = "ws://10.0.2.2:8080/ws?playerId=$playerId"
    stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl)

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
          if (data.type == "JOIN") {
            players.add(data.player)
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
