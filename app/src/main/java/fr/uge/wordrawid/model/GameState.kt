package fr.uge.wordrawid.model

import kotlinx.serialization.Serializable

@Serializable
enum class GameState {
  WAITING_FOR_PLAYERS,
  RUNNING,
  FINISHED,
}
