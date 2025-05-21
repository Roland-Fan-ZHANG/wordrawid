package fr.uge.wordrawid.screens.multi

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage

object StompClientManager {
  private const val TAG = "STOMP"
  private var stompClient: StompClient? = null
  private val disposables = CompositeDisposable()


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
