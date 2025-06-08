package fr.uge.wordrawid.dto

import fr.uge.wordrawid.model.Player
import kotlinx.serialization.Serializable

@Serializable
data class JoinGameResponse(
  val player: Player,
  val joinCode: String,
  val gameId: Long,
  val otherPlayers: List<Player>
)
