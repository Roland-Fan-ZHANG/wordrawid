package fr.uge.wordrawid.model

import kotlinx.serialization.Serializable

@Serializable
data class Lobby(
  val id: Long,
  val joinCode: String,
  val adminName: String,
  val players: List<Player>,
  var gameState: GameState,
  val gameManager: GameManager
)
